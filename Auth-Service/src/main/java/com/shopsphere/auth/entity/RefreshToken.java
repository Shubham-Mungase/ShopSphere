package com.shopsphere.auth.entity;


import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // 🔗 Relationship with CustomerEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private CustomerEntity user;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private String deviceInfo;
    private String ipAddress;

    // Constructors
    public RefreshToken() {}

    public RefreshToken(CustomerEntity user, String token, LocalDateTime expiryDate,
                        String deviceInfo, String ipAddress) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
        this.createdAt = LocalDateTime.now();
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
        this.revoked = false;
    }

    // Getters & Setters
    public UUID getId() { return id; }

    public CustomerEntity getUser() { return user; }
    public void setUser(CustomerEntity user) { this.user = user; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}