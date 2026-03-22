package com.shopsphere.shipping.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.shopsphere.shipping.dto.response.WarehouseResponse;

@Component
public class InventoryClientFallback implements InventoryClient {

    @Override
    public WarehouseResponse getWarehouse(UUID productId) {

        throw new RuntimeException(
            "Inventory service unavailable. Cannot select warehouse."
        );
    }
}