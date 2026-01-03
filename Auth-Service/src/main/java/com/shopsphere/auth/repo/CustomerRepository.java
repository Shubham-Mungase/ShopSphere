package com.shopsphere.auth.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopsphere.auth.entity.CustomerEntity;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer>{
	boolean existsByEmail(String email);
	
	Optional<CustomerEntity> findByEmail(String email);
}
