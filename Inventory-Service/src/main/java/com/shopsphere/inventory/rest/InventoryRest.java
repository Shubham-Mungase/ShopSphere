package com.shopsphere.inventory.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.inventory.dto.request.AddStockRequest;
import com.shopsphere.inventory.dto.request.UpdateThresholdRequest;
import com.shopsphere.inventory.dto.response.InventoryResponse;
import com.shopsphere.inventory.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryRest {

    private final InventoryService inventoryService;

    public InventoryRest(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventory(
            @PathVariable UUID productId) {

        InventoryResponse response = inventoryService.getInventory(productId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<InventoryResponse> addStock(
            @RequestBody AddStockRequest request) {

        InventoryResponse response = inventoryService.addStock(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{productId}/threshold")
    public ResponseEntity<InventoryResponse> updateThreshold(
            @PathVariable UUID productId,
            @RequestBody UpdateThresholdRequest request) {

        InventoryResponse response =
                inventoryService.updateThreshold(productId, request);

        return ResponseEntity.ok(response);
    }
}

