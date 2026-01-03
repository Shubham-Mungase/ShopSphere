package com.shopsphere.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopsphere.auth.dto.LoginRequest;
import com.shopsphere.auth.dto.RegisterRequest;
import com.shopsphere.auth.entity.CustomerEntity;
import com.shopsphere.auth.repo.CustomerRepository;
import com.shopsphere.auth.utils.JwtUtils;

@Service
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository repo;
	private final PasswordEncoder encoder;
	private final AuthenticationManager manager;
	private final JwtUtils jwtUtils;

	public CustomerServiceImpl(CustomerRepository repo, PasswordEncoder encoder, AuthenticationManager manager,
			JwtUtils jwtUtils) {
		this.repo = repo;
		this.encoder = encoder;
		this.manager = manager;
		this.jwtUtils = jwtUtils;
	}

	@Override
	public void register(RegisterRequest req) {
		if (repo.existsByEmail(req.getEmail())) {
			throw new RuntimeException("Email Already Registerd");
		}

		String encode = encoder.encode(req.getPassword());

		CustomerEntity entity = new CustomerEntity();

		entity.setEmail(req.getEmail());
		entity.setName(req.getName());
		entity.setPassword(encode);
		entity.setRole("USER");
		repo.save(entity);
	}

	@Override
	public String login(LoginRequest req) {

		 manager
				.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
		CustomerEntity customer = repo.findByEmail(req.getEmail()).orElseThrow();
		
		return jwtUtils.generateToken(customer.getEmail(),customer.getRole(),customer.getId());
	}

}
