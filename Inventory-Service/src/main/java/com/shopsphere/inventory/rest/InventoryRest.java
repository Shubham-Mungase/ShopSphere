package com.shopsphere.inventory.rest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.inventory.dto.request.AddStockRequest;
import com.shopsphere.inventory.dto.request.UpdateThresholdRequest;
import com.shopsphere.inventory.dto.response.ApiResponse;
import com.shopsphere.inventory.dto.response.InventoryResponse;
import com.shopsphere.inventory.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryRest {

	private final InventoryService inventoryService;

	public InventoryRest(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	// USER + ADMIN can view inventory
	@GetMapping("/{productId}")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<ApiResponse<InventoryResponse>> getInventory(@PathVariable UUID productId) {

		InventoryResponse response = inventoryService.getInventory(productId);

		ApiResponse<InventoryResponse> apiResponse = new ApiResponse<>();
		apiResponse.setMessage("Inventory fetched successfully");
		apiResponse.setData(response);
		apiResponse.setSuccess(true);

		return ResponseEntity.ok(apiResponse);
	}

	// ADMIN only → add stock
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<InventoryResponse>> addStock(@RequestBody AddStockRequest request) {

		InventoryResponse response = inventoryService.addStock(request);

		ApiResponse<InventoryResponse> apiResponse = new ApiResponse<>();
		apiResponse.setMessage("Stock added successfully");
		apiResponse.setData(response);
		apiResponse.setSuccess(true);

		return ResponseEntity.ok(apiResponse);
	}

	// ADMIN only → update threshold
	@PutMapping("/{productId}/threshold")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<InventoryResponse>> updateThreshold(@PathVariable UUID productId,
			@RequestBody UpdateThresholdRequest request) {

		InventoryResponse response = inventoryService.updateThreshold(productId, request);

		ApiResponse<InventoryResponse> apiResponse = new ApiResponse<>();
		apiResponse.setMessage("Threshold updated successfully");
		apiResponse.setData(response);
		apiResponse.setSuccess(true);

		return ResponseEntity.ok(apiResponse);
	}

	@PostMapping("/bulk")
	public Map<UUID, UUID> getWarehousesForProducts(@RequestBody List<UUID> productIds) {
		return inventoryService.getWarehousesForProducts(productIds);
	}
}