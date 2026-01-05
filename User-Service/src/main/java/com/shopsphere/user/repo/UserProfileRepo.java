package com.shopsphere.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.user.entity.UserProfileEntity;

public interface UserProfileRepo extends JpaRepository<UserProfileEntity, Integer> {

	UserProfileEntity findByUserId(String userId);
	
	boolean existsByUserId(String userId);
}
