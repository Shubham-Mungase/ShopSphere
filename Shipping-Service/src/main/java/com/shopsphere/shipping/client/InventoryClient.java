package com.shopsphere.shipping.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.shopsphere.shipping.config.FeignAuthInterceptor;

@FeignClient(name = "inventory-service", path = "/api/inventory", fallback = InventoryClientFallback.class, configuration = FeignAuthInterceptor.class)
public interface InventoryClient {

	@PostMapping("/bulk")
	Map<UUID, UUID> getWarehousesForProducts(@RequestBody List<UUID> productIds);
}