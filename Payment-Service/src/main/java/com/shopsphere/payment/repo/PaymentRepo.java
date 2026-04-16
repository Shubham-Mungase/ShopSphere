package com.shopsphere.payment.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopsphere.payment.entity.PayementEntity;

@Repository
public interface PaymentRepo extends JpaRepository<PayementEntity, UUID>{

	 Optional<PayementEntity>  findByOrderId(UUID orderId);
	
	 Optional<PayementEntity>  findByGatewayOrderId(String gatwayOrderId);
}
