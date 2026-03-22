package com.shopsphere.shipping.client;


import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopsphere.shipping.dto.response.WarehouseResponse;


@FeignClient(name = "Inventory-Service",fallback = InventoryClientFallback.class)
public interface InventoryClient {

    @GetMapping("/internal/warehouse/select/{productId}")
    WarehouseResponse getWarehouse(@PathVariable UUID productId);

}