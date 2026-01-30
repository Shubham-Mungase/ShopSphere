package com.shopsphere.order.domain.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.order.domain.entity.OrderEntity;

public interface OrderRepo extends JpaRepository<OrderEntity, UUID>{
	
	List<OrderEntity> findByUserId(UUID userId);
	
	List<OrderEntity> findByStatus(String status);
 
}
