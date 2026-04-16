package com.shopsphere.order.domain.filter;

import java.util.UUID;

public class UserContext {

    private UUID userId;
    private String username;
    private String role;

    public UserContext(UUID userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public UUID getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}