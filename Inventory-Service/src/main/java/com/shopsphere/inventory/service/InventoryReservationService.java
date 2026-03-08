package com.shopsphere.inventory.service;

import com.shopsphere.inventory.dto.request.ConfirmStockRequest;
import com.shopsphere.inventory.dto.request.ReleaseStockRequest;
import com.shopsphere.inventory.dto.request.ReserveStockRequest;

public interface InventoryReservationService {
	
	void confirmStock(ConfirmStockRequest request);
	void reserveStock(ReserveStockRequest request);
	void releaseStock(ReleaseStockRequest request);
}
