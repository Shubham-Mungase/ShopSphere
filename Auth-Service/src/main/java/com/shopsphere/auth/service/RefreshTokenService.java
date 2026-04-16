package com.shopsphere.auth.service;

import com.shopsphere.auth.dto.LogoutRequest;
import com.shopsphere.auth.dto.RefreshTokenRequest;
import com.shopsphere.auth.dto.RefreshTokenResponse;
import com.shopsphere.auth.entity.CustomerEntity;

public interface RefreshTokenService {

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
    
    public String createRefreshToken(CustomerEntity user, String deviceInfo, String ipAddress) ;
    void logout(LogoutRequest request);

    void logoutAll(String userEmail);
}