package com.shopsphere.shipping.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.shopsphere.shipping.exceptions.InventoryServiceException;

@Component
public class InventoryClientFallback implements InventoryClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryClientFallback.class);

  

	@Override
	public Map<UUID, UUID> getWarehousesForProducts(List<UUID> productIds) {
		 
		  log.error("Inventory service fallback triggered for productId={}", productIds);

		throw new InventoryServiceException(
	                "Inventory service unavailable. Cannot select warehouse for productId=" + productIds,
	                null
	        );
	}
}