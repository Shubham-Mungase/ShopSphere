package com.shopsphere.user.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.user.entity.AddressEntity;

public interface AddressRepo extends JpaRepository<AddressEntity, UUID> {

	AddressEntity findByUserId(UUID userId);

	AddressEntity findByUserIdAndIsDefaultTrue(UUID userId);
	
	long countByUserId(UUID userId);
	
	List<AddressEntity> findAllByUserId(UUID userId);
}
