package com.shopsphere.product.filter;

public class CustomAuthUserPrinciple {

	private final String userId;
	private final String email;

	public CustomAuthUserPrinciple(String userId, String email) {
		this.userId = userId;
		this.email = email;

	}

	public String getUserId() {
		return userId;
	}

	public String getEmail() {
		return email;
	}

}
