package com.shopsphere.auth.service;

import com.shopsphere.auth.dto.LoginRequest;
import com.shopsphere.auth.dto.RegisterRequest;

public interface CustomerService {
	
	public void register(RegisterRequest req);
	
	public String login(LoginRequest req);

}
