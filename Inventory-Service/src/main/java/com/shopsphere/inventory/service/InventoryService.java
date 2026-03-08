package com.shopsphere.inventory.service;

import java.util.UUID;

import com.shopsphere.inventory.dto.request.AddStockRequest;
import com.shopsphere.inventory.dto.request.UpdateThresholdRequest;
import com.shopsphere.inventory.dto.response.InventoryResponse;

public interface InventoryService {
	 InventoryResponse getInventory(UUID productId);

	    InventoryResponse addStock(AddStockRequest request);

	    InventoryResponse updateThreshold(UUID productId, UpdateThresholdRequest request);
}
