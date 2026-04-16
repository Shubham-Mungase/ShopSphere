package com.shopsphere.auth.dto;

public class LogoutRequest {

    private String refreshToken;
    
    public LogoutRequest(String refreshToken) {
		super();
		this.refreshToken = refreshToken;
	}

	public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}