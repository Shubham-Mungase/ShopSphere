package com.shopsphere.user.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.user.entity.UserProfileEntity;

public interface UserProfileRepo extends JpaRepository<UserProfileEntity, UUID> {

	UserProfileEntity findByUserId(UUID userId);
	
	boolean existsByUserId(UUID userId);
}
