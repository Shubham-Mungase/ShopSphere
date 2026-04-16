package com.shopsphere.inventory.service;


import java.util.List;
import java.util.UUID;

import com.shopsphere.inventory.dto.request.CreateWarehouseRequest;
import com.shopsphere.inventory.dto.response.WarehouseResponse;

public interface WarehouseService {

    
    WarehouseResponse createWarehouse(CreateWarehouseRequest request);

    WarehouseResponse getWarehouse(UUID warehouseId);

    List<WarehouseResponse> getAllWarehouses();

    WarehouseResponse selectWarehouse(UUID productId);
}