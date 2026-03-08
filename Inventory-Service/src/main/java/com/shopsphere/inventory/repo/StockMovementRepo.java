package com.shopsphere.inventory.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopsphere.inventory.entity.StockMovement;
import com.shopsphere.inventory.enums.MovementType;

@Repository
public interface StockMovementRepo extends JpaRepository<StockMovement, UUID> {

	// Get movement history for product
	List<StockMovement> findByProductIdOrderByCreatedAtDesc(UUID productId);

	// Get movement by order
	List<StockMovement> findByOrderId(UUID orderId);

	// Get movement by type
	List<StockMovement> findByType(MovementType type);

}
