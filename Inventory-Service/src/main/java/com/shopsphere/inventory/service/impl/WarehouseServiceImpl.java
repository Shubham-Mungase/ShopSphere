package com.shopsphere.inventory.service.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.shopsphere.inventory.dto.request.CreateWarehouseRequest;
import com.shopsphere.inventory.dto.response.WarehouseResponse;
import com.shopsphere.inventory.entity.Inventory;
import com.shopsphere.inventory.entity.Warehouse;
import com.shopsphere.inventory.exception.ProductOutOfStockException;
import com.shopsphere.inventory.exception.WarehouseNotFoundException;
import com.shopsphere.inventory.exception.WarehouseServiceException;
import com.shopsphere.inventory.repo.InventoryRepo;
import com.shopsphere.inventory.repo.WarehouseRepo;
import com.shopsphere.inventory.service.WarehouseService;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    private static final Logger log = LoggerFactory.getLogger(WarehouseServiceImpl.class);

    private final WarehouseRepo warehouseRepo;
    private final InventoryRepo inventoryRepo;

    public WarehouseServiceImpl(WarehouseRepo warehouseRepo,
                                InventoryRepo inventoryRepo) {
        this.warehouseRepo = warehouseRepo;
        this.inventoryRepo = inventoryRepo;
    }

    @Override
    public WarehouseResponse createWarehouse(CreateWarehouseRequest request) {

        log.info("Creating warehouse: name={}, city={}", request.getName(), request.getCity());

        try {
            Warehouse warehouse = new Warehouse();

            warehouse.setName(request.getName());
            warehouse.setCity(request.getCity());
            warehouse.setState(request.getState());
            warehouse.setPincode(request.getPincode());
            warehouse.setLatitude(request.getLatitude());
            warehouse.setLongitude(request.getLongitude());

            Warehouse saved = warehouseRepo.save(warehouse);

            log.info("Warehouse created successfully with id={}", saved.getId());

            return map(saved);

        } catch (Exception e) {
            log.error("Failed to create warehouse: {}", request.getName(), e);
            throw new WarehouseServiceException("Failed to create warehouse", e);
        }
    }

    @Override
    public WarehouseResponse getWarehouse(UUID warehouseId) {

        log.info("Fetching warehouse with id={}", warehouseId);

        Warehouse warehouse = warehouseRepo.findById(warehouseId)
                .orElseThrow(() -> {
                    log.error("Warehouse not found with id={}", warehouseId);
                    return new WarehouseNotFoundException("Warehouse not found");
                });

        log.info("Warehouse fetched successfully id={}", warehouseId);

        return map(warehouse);
    }

    @Override
    public List<WarehouseResponse> getAllWarehouses() {

        log.info("Fetching all warehouses");

        List<WarehouseResponse> list = warehouseRepo.findAll()
                .stream()
                .map(this::map)
                .toList();

        log.info("Total warehouses found={}", list.size());

        return list;
    }

    @Override
    public WarehouseResponse selectWarehouse(UUID productId) {

        log.info("Selecting warehouse for productId={}", productId);

        List<Inventory> inventories = inventoryRepo.findAllByProductId(productId);

        Inventory selected = inventories.stream()
                .filter(inv -> inv.getAvailableStock() > 0)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Product out of stock for productId={}", productId);
                    return new ProductOutOfStockException("Product out of stock");
                });

        log.info("Warehouse selected for productId={}, warehouseId={}",
                productId, selected.getWarehouse().getId());

        return map(selected.getWarehouse(), selected.getAvailableStock());
    }

    private WarehouseResponse map(Warehouse warehouse) {

        WarehouseResponse response = new WarehouseResponse();

        response.setWarehouseId(warehouse.getId());
        response.setWarehouseName(warehouse.getName());
        response.setLocation(warehouse.getCity());

        return response;
    }

    private WarehouseResponse map(Warehouse warehouse, Integer stock) {

        WarehouseResponse response = new WarehouseResponse();

        response.setWarehouseId(warehouse.getId());
        response.setWarehouseName(warehouse.getName());
        response.setLocation(warehouse.getCity());
        response.setAvailableStock(stock);

        return response;
    }
}