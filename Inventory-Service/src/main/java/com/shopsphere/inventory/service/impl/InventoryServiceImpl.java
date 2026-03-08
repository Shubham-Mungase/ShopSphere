package com.shopsphere.inventory.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.inventory.dto.request.AddStockRequest;
import com.shopsphere.inventory.dto.request.UpdateThresholdRequest;
import com.shopsphere.inventory.dto.response.InventoryResponse;
import com.shopsphere.inventory.entity.Inventory;
import com.shopsphere.inventory.repo.InventoryRepo;
import com.shopsphere.inventory.service.InventoryService;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

	private final InventoryRepo repo;

	public InventoryServiceImpl(InventoryRepo repo) {
		super();
		this.repo = repo;
	}

	@Override
	public InventoryResponse getInventory(UUID productId) {

		Inventory inventory = repo.findByProductId(productId)
				.orElseThrow(() -> new RuntimeException("Product Not Found"));

		return map(inventory);

	}

	@Override
	public InventoryResponse addStock(AddStockRequest request) {

		Inventory inventory = repo.findByProductId(request.getProductId())
				.orElseThrow(() -> new RuntimeException("Product Not Found"));
		inventory.setAvailableStock(inventory.getAvailableStock()+request.getQuantity());
		inventory.updateStatus();
		Inventory inventory2 = repo.save(inventory);
		return map(inventory2);
	}

	@Override
	public InventoryResponse updateThreshold(UUID productId, UpdateThresholdRequest request) {
		// TODO Auto-generated method stub
		
		Inventory inventory = repo.findByProductId(productId)
				.orElseThrow(() -> new RuntimeException("Product Not Found"));
		
		inventory.setLowStockThreshold(request.getLowStockThreshold());
		inventory.updateStatus();
		Inventory inventory2 = repo.save(inventory);
				
		return map(inventory2);
	}

	private InventoryResponse map(Inventory inventory) {

		InventoryResponse response=new InventoryResponse();
		
		response.setAvailableStock(inventory.getAvailableStock());
		response.setLowStockThreshold(inventory.getLowStockThreshold());
		response.setProductId(inventory.getProductId());
		response.setReservedStock(inventory.getReservedStock());
		response.setStatus(inventory.getStatus());
		response.setProductName(inventory.getProductName());
		return response;
	}

}
