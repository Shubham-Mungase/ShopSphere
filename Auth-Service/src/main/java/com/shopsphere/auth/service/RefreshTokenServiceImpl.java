package com.shopsphere.auth.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.shopsphere.auth.dto.LogoutRequest;
import com.shopsphere.auth.dto.RefreshTokenRequest;
import com.shopsphere.auth.dto.RefreshTokenResponse;
import com.shopsphere.auth.entity.CustomerEntity;
import com.shopsphere.auth.entity.RefreshToken;
import com.shopsphere.auth.exception.TokenException;
import com.shopsphere.auth.repo.CustomerRepository;
import com.shopsphere.auth.repo.RefreshTokenRepository;
import com.shopsphere.auth.utils.JwtUtils;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final JwtUtils jwtService;
    private final CustomerRepository customerRepository;
    private final RedisTemplate<String , String> redisTemplate;

    private static final long REFRESH_TOKEN_VALIDITY_DAYS = 7;

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);


    public RefreshTokenServiceImpl(RefreshTokenRepository repo, JwtUtils jwtService,
			CustomerRepository customerRepository, RedisTemplate<String, String> redisTemplate) {
		super();
		this.repo = repo;
		this.jwtService = jwtService;
		this.customerRepository = customerRepository;
		this.redisTemplate = redisTemplate;
	}

	// ===================== PUBLIC METHODS =====================

    @Transactional
    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {

        logger.info("Refresh token request received");

        RefreshToken oldToken = validateRefreshToken(request.getRefreshToken());

        UUID id = oldToken.getUser().getId();
        
        logger.info("Refresh token validated for user: {}", oldToken.getUser().getEmail());

        String newRefreshToken = rotateRefreshToken(oldToken);

        
        logger.info("Refresh token rotated for user: {}", oldToken.getUser().getEmail());

        String newAccessToken = jwtService.generateToken(oldToken.getUser());

        logger.info("New access token generated for user: {}", oldToken.getUser().getEmail());

        redisTemplate.delete("access:"+id);
        redisTemplate.opsForValue().set("access:"+id, newAccessToken,15,TimeUnit.MINUTES);


        logger.info("Access token updated in Redis for user: {}", id);
        
        
        
        return new RefreshTokenResponse(newAccessToken, newRefreshToken); // raw token returned
    }

    @Transactional
    @Override
    public String createRefreshToken(CustomerEntity user,
                                     String deviceInfo,
                                     String ipAddress) {

        String rawToken = UUID.randomUUID().toString();
        String hashedToken = hash(rawToken);

        RefreshToken refreshToken = new RefreshToken(
                user,
                hashedToken,
                LocalDateTime.now().plusDays(REFRESH_TOKEN_VALIDITY_DAYS),
                deviceInfo,
                ipAddress
        );

        repo.save(refreshToken);

        return rawToken; //ONLY raw goes outside
    }

    @Transactional
    @Override
    public void logout(LogoutRequest request) {

    	 
        logger.info("Logout request received");

        String hashedToken = hash(request.getRefreshToken());

        int updated = repo.revokeByToken(hashedToken);

        if (updated == 0) {
            logger.warn("Token not found or already revoked during logout");
            return; 	
        }

        logger.info("Logout successful (single device)");
    }

    @Transactional
    @Override
    public void logoutAll(String accessToken) {

        String email = jwtService.extractUsername(accessToken);

        logger.info("Logout all request for user: {}", email);

        CustomerEntity user = customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", email);
                    return new TokenException("User not found");
                });

        repo.revokeAllTokensByUser(user);

        logger.info("All sessions revoked for user: {}", email);
    }

    // ===================== INTERNAL METHODS =====================

    private RefreshToken validateRefreshToken(String rawToken) {

        logger.debug("Validating refresh token");


        
        logger.info("RAW TOKEN FROM COOKIE: {}", rawToken);

        String hashedToken = hash(rawToken);

        logger.info("HASHED TOKEN: {}", hashedToken);
        RefreshToken refreshToken = repo.findByToken(hashedToken)
                .orElseThrow(() -> {
                    logger.error("Invalid refresh token");
                    return new TokenException("Invalid refresh token");
                });

        if (refreshToken.isRevoked()) {
            logger.warn("Attempt to use revoked token for user: {}",
                    refreshToken.getUser().getEmail());
            throw new TokenException("Refresh token is revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            logger.warn("Expired refresh token used by user: {}",
                    refreshToken.getUser().getEmail());
            throw new TokenException("Refresh token expired");
        }

        return refreshToken;
    }

    private String rotateRefreshToken(RefreshToken oldToken) {

        // revoke old token
        oldToken.setRevoked(true);
        repo.save(oldToken);

        //  create new token
        return createRefreshToken(
                oldToken.getUser(),
                oldToken.getDeviceInfo(),
                oldToken.getIpAddress()
        );
    }

    // ===================== UTIL =====================

    private String hash(String token) {
        return DigestUtils.md5DigestAsHex(token.getBytes(StandardCharsets.UTF_8));
    }
}