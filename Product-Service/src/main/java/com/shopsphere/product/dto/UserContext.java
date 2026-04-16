package com.shopsphere.product.dto;


public class UserContext {

    private String userId;
    private String username;
    private String role;

    public UserContext(String userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}