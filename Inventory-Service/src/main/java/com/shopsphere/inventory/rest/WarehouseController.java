package com.shopsphere.inventory.rest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.inventory.dto.request.CreateWarehouseRequest;
import com.shopsphere.inventory.dto.response.ApiResponse;
import com.shopsphere.inventory.dto.response.WarehouseResponse;
import com.shopsphere.inventory.service.WarehouseService;

@RestController
@RequestMapping("/api/inventory/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    // USER + ADMIN
    @GetMapping("/select/{productId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<WarehouseResponse>> selectWarehouse(@PathVariable UUID productId) {

        WarehouseResponse response = warehouseService.selectWarehouse(productId);

        ApiResponse<WarehouseResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Warehouse selected successfully");
        apiResponse.setData(response);
        apiResponse.setSuccess(true);

        return ResponseEntity.ok(apiResponse);
    }
    
    

    // ADMIN only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WarehouseResponse>> createWarehouse(
            @RequestBody CreateWarehouseRequest request) {

        WarehouseResponse response = warehouseService.createWarehouse(request);

        ApiResponse<WarehouseResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Warehouse created successfully");
        apiResponse.setData(response);
        apiResponse.setSuccess(true);

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    // USER + ADMIN
    @GetMapping("/{warehouseId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getWarehouse(
            @PathVariable UUID warehouseId) {

        WarehouseResponse response = warehouseService.getWarehouse(warehouseId);

        ApiResponse<WarehouseResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Warehouse fetched successfully");
        apiResponse.setData(response);
        apiResponse.setSuccess(true);

        return ResponseEntity.ok(apiResponse);
    }

    // ADMIN only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<WarehouseResponse>>> getAllWarehouses() {

        List<WarehouseResponse> response = warehouseService.getAllWarehouses();

        ApiResponse<List<WarehouseResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("All warehouses fetched successfully");
        apiResponse.setData(response);
        apiResponse.setSuccess(true);

        return ResponseEntity.ok(apiResponse);
    }
}