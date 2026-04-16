package com.shopsphere.inventory.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.inventory.dto.request.AddStockRequest;
import com.shopsphere.inventory.dto.request.UpdateThresholdRequest;
import com.shopsphere.inventory.dto.response.InventoryResponse;
import com.shopsphere.inventory.entity.Inventory;
import com.shopsphere.inventory.entity.Warehouse;
import com.shopsphere.inventory.exception.InventoryNotFoundException;
import com.shopsphere.inventory.exception.WarehouseNotFoundException;
import com.shopsphere.inventory.repo.InventoryRepo;
import com.shopsphere.inventory.repo.WarehouseRepo;
import com.shopsphere.inventory.service.InventoryService;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final InventoryRepo repo;
    private final WarehouseRepo warehouseRepo;

    public InventoryServiceImpl(InventoryRepo repo, WarehouseRepo warehouseRepo) {
        this.repo = repo;
        this.warehouseRepo = warehouseRepo;
    }

    @Override
    public InventoryResponse getInventory(UUID productId) {

        log.info("Fetching inventory for productId={}", productId);

        Inventory inventory = repo.findByProductId(productId)
                .orElseThrow(() -> {
                    log.error("Inventory not found for productId={}", productId);
                    return new InventoryNotFoundException("Product Not Found");
                });

        log.info("Inventory fetched successfully for productId={}", productId);
        return map(inventory);
    }

    @Override
    public InventoryResponse addStock(AddStockRequest request) {

        log.info("Adding stock: productId={}, warehouseId={}, quantity={}",
                request.getProductId(), request.getWarehouseId(), request.getQuantity());

        Inventory inventory = repo.findByProductId(request.getProductId())
                .orElseThrow(() -> {
                    log.error("Inventory not found for productId={}", request.getProductId());
                    return new InventoryNotFoundException("Product Not Found");
                });

        Warehouse warehouse = warehouseRepo.findById(request.getWarehouseId())
                .orElseThrow(() -> {
                    log.error("Warehouse not found for warehouseId={}", request.getWarehouseId());
                    return new WarehouseNotFoundException("Warehouse Not Found");
                });

        inventory.setAvailableStock(inventory.getAvailableStock() + request.getQuantity());

        inventory.setWarehouse(warehouse);

        inventory.updateStatus();

        Inventory saved = repo.save(inventory);

        log.info("Stock added successfully for productId={}, newStock={}",
                saved.getProductId(), saved.getAvailableStock());

        return map(saved);
    }

    @Override
    public InventoryResponse updateThreshold(UUID productId, UpdateThresholdRequest request) {

        log.info("Updating threshold for productId={}, newThreshold={}",
                productId, request.getLowStockThreshold());

        Inventory inventory = repo.findByProductId(productId)
                .orElseThrow(() -> {
                    log.error("Inventory not found for productId={}", productId);
                    return new InventoryNotFoundException("Product Not Found");
                });

        inventory.setLowStockThreshold(request.getLowStockThreshold());

        inventory.updateStatus();

        Inventory saved = repo.save(inventory);

        log.info("Threshold updated successfully for productId={}", productId);

        return map(saved);
    }

    private InventoryResponse map(Inventory inventory) {

        InventoryResponse response = new InventoryResponse();

        response.setAvailableStock(inventory.getAvailableStock());
        response.setLowStockThreshold(inventory.getLowStockThreshold());
        response.setProductId(inventory.getProductId());
        response.setReservedStock(inventory.getReservedStock());
        response.setStatus(inventory.getStatus());
        response.setProductName(inventory.getProductName());

        if (inventory.getWarehouse() != null) {
            response.setWarehouseId(inventory.getWarehouse().getId());
        }

        return response;
    }
    
    @Override
    public Map<UUID, UUID> getWarehousesForProducts(List<UUID> productIds) {

        //  Fetch in ONE DB query
        List<Inventory> inventories =
                repo.findByProductIdIn(productIds);

        Map<UUID, UUID> result = new HashMap<>();

        for (Inventory inv : inventories) {

            if (inv.getWarehouse() != null) {
                result.put(
                        inv.getProductId(),
                        inv.getWarehouse().getId() );
            }
        }

        return result;
    }
}