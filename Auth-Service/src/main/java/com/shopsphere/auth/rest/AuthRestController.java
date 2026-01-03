package com.shopsphere.auth.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.auth.dto.LoginRequest;
import com.shopsphere.auth.dto.RegisterRequest;
import com.shopsphere.auth.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthRestController {
	private CustomerService customerService;

	public AuthRestController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@PostMapping("/register")
	public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {

		customerService.register(request);
		return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {

		String token = customerService.login(request);
		return new ResponseEntity<>(token, HttpStatus.OK);
	}

	@GetMapping("/test")
	public String test() {
		return "JWT is working!";
	}

}
