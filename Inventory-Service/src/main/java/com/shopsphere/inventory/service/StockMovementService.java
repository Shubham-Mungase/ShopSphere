package com.shopsphere.inventory.service;

import java.util.List;
import java.util.UUID;

import com.shopsphere.inventory.dto.response.StockMovementResponse;
import com.shopsphere.inventory.enums.MovementType;

public interface StockMovementService {
	
  
	void recordMovement(
            UUID productId,
            UUID orderId,
            MovementType type,
            Integer quantity,
            String reason
    );

    List<StockMovementResponse> getMovements(UUID productId);
}
