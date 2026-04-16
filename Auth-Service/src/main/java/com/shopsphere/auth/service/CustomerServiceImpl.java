package com.shopsphere.auth.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.auth.dto.ChangePasswordRequest;
import com.shopsphere.auth.dto.ForgotPasswordRequest;
import com.shopsphere.auth.dto.LoginRequest;
import com.shopsphere.auth.dto.RefreshTokenResponse;
import com.shopsphere.auth.dto.RegisterRequest;
import com.shopsphere.auth.dto.ResetPasswordRequest;
import com.shopsphere.auth.dto.event.OtpGeneratedEvent;
import com.shopsphere.auth.dto.event.PasswordChangedEvent;
import com.shopsphere.auth.dto.event.UserRegisteredEvent;
import com.shopsphere.auth.entity.CustomerEntity;
import com.shopsphere.auth.entity.RefreshToken;
import com.shopsphere.auth.exception.AuthenticationFailedException;
import com.shopsphere.auth.exception.CustomerNotFoundException;
import com.shopsphere.auth.exception.EmailAlreadyExistsException;
import com.shopsphere.auth.exception.InvalidOtpException;
import com.shopsphere.auth.exception.OtpExpiredException;
import com.shopsphere.auth.repo.CustomerRepository;
import com.shopsphere.auth.utils.JwtUtils;
import com.shopsphere.auth.utils.OtpUtil;

@Service
public class CustomerServiceImpl implements CustomerService {


    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository repo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager manager;
    private final JwtUtils jwtUtils;
    private final OutboxService outboxService;
    private final RefreshTokenService refreshTokenService;
    private final RedisTemplate<String, String> redisTemplate;
    
    


	public CustomerServiceImpl(CustomerRepository repo, PasswordEncoder encoder, AuthenticationManager manager,
			JwtUtils jwtUtils, OutboxService outboxService, RefreshTokenService refreshTokenService,
			RedisTemplate<String, String> redisTemplate) {
		super();
		this.repo = repo;
		this.encoder = encoder;
		this.manager = manager;
		this.jwtUtils = jwtUtils;
		this.outboxService = outboxService;
		this.refreshTokenService = refreshTokenService;
		this.redisTemplate = redisTemplate;
	}

	// ================= REGISTER =================

    @Transactional
    @Override
    public void register(RegisterRequest req) {

        log.info("Register request email={}", req.getEmail());

        if (repo.existsByEmail(req.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        CustomerEntity entity = new CustomerEntity();
        entity.setEmail(req.getEmail());
        entity.setName(req.getName());
        entity.setPassword(encoder.encode(req.getPassword()));
        entity.setRole("USER");

        repo.save(entity);

        //  EVENT
        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setTimestamp(System.currentTimeMillis());
        event.setEmail(entity.getEmail());
        event.setName(entity.getName());
        event.setPurpose("USER_REGISTERED");

        outboxService.saveEvent(
                "AUTHENTICATION",
                entity.getId().toString(),
                "USER_REGISTERED",
                event
        );

        log.info("User registered + event stored email={}", req.getEmail());
    }

    // ================= LOGIN =================

    @Override
    public RefreshTokenResponse login(LoginRequest req) {

        log.info("Login attempt for email={}", req.getEmail());

        try {
            manager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getEmail(),
                            req.getPassword()
                    )
            );
        } catch (Exception ex) {
            log.error("Authentication failed for email={}", req.getEmail(), ex);
            throw new AuthenticationFailedException("Invalid email or password", ex);
        }

        CustomerEntity customer = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> {
                    log.error("Customer not found after authentication email={}", req.getEmail());
                    return new CustomerNotFoundException("Customer not found");
                });

        //  1. Generate Access Token
        String accessToken = jwtUtils.generateToken(customer);

        //  2. CREATE & SAVE REFRESH TOKEN 
        String refreshToken = refreshTokenService.createRefreshToken(
                customer,
                 req.getDeviceInfo(),        
                req.getIpAddress()  
        );
//extract user id and map to redis cache db
        UUID id = customer.getId();

     // DELETE old token first 
     redisTemplate.delete("access:" + id);

     // store new token
     redisTemplate.opsForValue().set(
             "access:" + id,
             accessToken,
             15,
             TimeUnit.MINUTES
     );

     String stored = redisTemplate.opsForValue().get("access:" + id);
     log.info(" Redis after login: {}", stored);
        log.info("Login successful for email={}", req.getEmail());

        // 3. Return BOTH tokens
        return new RefreshTokenResponse(
                accessToken,
                refreshToken
        );
    }
    
    @Transactional
    @Override
    public void forgotPassword(ForgotPasswordRequest request) {

        CustomerEntity customer = repo.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomerNotFoundException("Not found"));

        String otp = OtpUtil.generateOtp();

        customer.setOtp(otp);
        customer.setOtpExpiry(System.currentTimeMillis() + 5 * 60 * 1000);

        repo.save(customer);

        //  EVENT
        OtpGeneratedEvent event = new OtpGeneratedEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setTimestamp(System.currentTimeMillis());
        event.setEmail(customer.getEmail());
        event.setOtp(otp);
        event.setPurpose("RESET_PASSWORD");

        outboxService.saveEvent(
                "AUTHENTICATION",
                customer.getId().toString(),
                "OTP_GENERATED",
                event
        );

        log.info("OTP generated + event saved email={}", request.getEmail());
    }
    
    @Transactional
    @Override
    public void resetPassword(ResetPasswordRequest request) {

        CustomerEntity customer = repo.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomerNotFoundException("Not found"));

        if (!customer.getOtp().equals(request.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        if (customer.getOtpExpiry() < System.currentTimeMillis()) {
            throw new OtpExpiredException("OTP expired");
        }

        customer.setPassword(encoder.encode(request.getNewPassword()));
        customer.setOtp(null);
        customer.setOtpExpiry(null);

        repo.save(customer);

        //  EVENT
        PasswordChangedEvent event = new PasswordChangedEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setTimestamp(System.currentTimeMillis());
        event.setEmail(customer.getEmail());
        event.setPurpose("PASSWORD_CHANGED");
        

        outboxService.saveEvent(
                "AUTHENTICATION",
                customer.getId().toString(),
                "PASSWORD_CHANGED",
                event
        );
    }
    
    @Override
    public void changePassword(ChangePasswordRequest request) {

        log.info("Change password request for email={}", request.getEmail());

        CustomerEntity customer = repo.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        if (!encoder.matches(request.getOldPassword(), customer.getPassword())) {
            log.warn("Old password mismatch for email={}", request.getEmail());
            throw new AuthenticationFailedException("Invalid old password",null);
        }

        customer.setPassword(encoder.encode(request.getNewPassword()));

        repo.save(customer);
        PasswordChangedEvent event = new PasswordChangedEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setTimestamp(System.currentTimeMillis());
        event.setEmail(customer.getEmail());
        event.setPurpose("PASSWORD_CHANGED");
        

        outboxService.saveEvent(
                "AUTHENTICATION",
                customer.getId().toString(),
                "PASSWORD_CHANGED",
                event
        );
        

        log.info("Password changed successfully for email={}", request.getEmail());
    }
}