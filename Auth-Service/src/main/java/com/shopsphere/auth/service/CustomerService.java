package com.shopsphere.auth.service;

import com.shopsphere.auth.dto.ChangePasswordRequest;
import com.shopsphere.auth.dto.ForgotPasswordRequest;
import com.shopsphere.auth.dto.LoginRequest;
import com.shopsphere.auth.dto.RefreshTokenResponse;
import com.shopsphere.auth.dto.RegisterRequest;
import com.shopsphere.auth.dto.ResetPasswordRequest;

public interface CustomerService {

	public void register(RegisterRequest req);

	public RefreshTokenResponse login(LoginRequest req);

	public void forgotPassword(ForgotPasswordRequest request);
	
	 public void resetPassword(ResetPasswordRequest request);
	 
	 public void changePassword(ChangePasswordRequest request);

}
