package com.shopsphere.inventory.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.inventory.dto.response.StockMovementResponse;
import com.shopsphere.inventory.entity.StockMovement;
import com.shopsphere.inventory.enums.MovementType;
import com.shopsphere.inventory.repo.StockMovementRepo;
import com.shopsphere.inventory.service.StockMovementService;

@Service
@Transactional
public class StockMovementServiceImpl implements StockMovementService {

	private final StockMovementRepo movementRepository;

	public StockMovementServiceImpl(StockMovementRepo movementRepository) {
		super();
		this.movementRepository = movementRepository;
	}

	@Override
	public void recordMovement(UUID productId, UUID orderId, MovementType type, Integer quantity, String reason) {

		StockMovement movement = new StockMovement();

		movement.setOrderId(orderId);
		movement.setProductId(productId);
		movement.setQuantity(quantity);
		movement.setReason(reason);
		movement.setType(type);

		movementRepository.save(movement);

	}

	@Override
	public List<StockMovementResponse> getMovements(UUID productId) {

		List<StockMovementResponse> list = new ArrayList<>();

		List<StockMovement> stockList = movementRepository.findByProductIdOrderByCreatedAtDesc(productId);

		for (StockMovement stockMovement : stockList) {
			StockMovementResponse response = new StockMovementResponse();

			response.setOrderId(stockMovement.getOrderId());
			response.setProductId(stockMovement.getProductId());
			response.setQuantity(stockMovement.getQuantity());
			response.setReason(stockMovement.getReason());
			response.setType(stockMovement.getType());
			list.add(response);
		}
		return list;
	}

}
