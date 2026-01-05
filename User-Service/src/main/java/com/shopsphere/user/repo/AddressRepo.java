package com.shopsphere.user.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.user.entity.AddressEntity;

public interface AddressRepo extends JpaRepository<AddressEntity, Integer> {

	AddressEntity findByUserId(String userId);

	AddressEntity findByUserIdAndIsDefaultTrue(String userId);
	
	long countByUserId(String userId);
	
	List<AddressEntity> findAllByUserId(String userId);
}
