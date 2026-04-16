package com.shopsphere.auth.repo;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.shopsphere.auth.entity.RefreshToken;
import com.shopsphere.auth.entity.CustomerEntity;

import jakarta.transaction.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    // ✅ Find by token (used in refresh API)
    Optional<RefreshToken> findByToken(String token);

    // ✅ Get all active tokens for a user
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND rt.revoked = false")
    List<RefreshToken> findAllActiveTokensByUser(CustomerEntity user);

    // ✅ Revoke all tokens of a user (logout from all devices)
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user AND rt.revoked = false")
    int revokeAllTokensByUser(CustomerEntity user);

    // ✅ Revoke single token (logout from current device)
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.token = :token")
    int revokeByToken(String token);

    // ✅ Delete expired tokens (cleanup job)
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    int deleteExpiredTokens(LocalDateTime now);
}