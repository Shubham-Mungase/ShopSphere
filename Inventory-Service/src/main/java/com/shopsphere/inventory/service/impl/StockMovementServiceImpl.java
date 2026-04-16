package com.shopsphere.inventory.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.inventory.dto.response.StockMovementResponse;
import com.shopsphere.inventory.entity.StockMovement;
import com.shopsphere.inventory.enums.MovementType;
import com.shopsphere.inventory.exception.StockMovementException;
import com.shopsphere.inventory.repo.StockMovementRepo;
import com.shopsphere.inventory.service.StockMovementService;

@Service
@Transactional
public class StockMovementServiceImpl implements StockMovementService {

    private static final Logger log = LoggerFactory.getLogger(StockMovementServiceImpl.class);

    private final StockMovementRepo movementRepository;

    public StockMovementServiceImpl(StockMovementRepo movementRepository) {
        this.movementRepository = movementRepository;
    }

    @Override
    public void recordMovement(UUID productId, UUID orderId, MovementType type, Integer quantity, String reason) {

        log.info("Recording stock movement: productId={}, orderId={}, type={}, quantity={}",
                productId, orderId, type, quantity);

        try {
            StockMovement movement = new StockMovement();

            movement.setOrderId(orderId);
            movement.setProductId(productId);
            movement.setQuantity(quantity);
            movement.setReason(reason);
            movement.setType(type);

            movementRepository.save(movement);

            log.debug("Stock movement saved successfully for productId={}", productId);

        } catch (Exception e) {
            log.error("Failed to record stock movement for productId={}, orderId={}", productId, orderId, e);
            throw new StockMovementException("Failed to record stock movement", e);
        }
    }

    @Override
    public List<StockMovementResponse> getMovements(UUID productId) {

        log.info("Fetching stock movements for productId={}", productId);

        try {
            List<StockMovementResponse> list = new ArrayList<>();

            List<StockMovement> stockList =
                    movementRepository.findByProductIdOrderByCreatedAtDesc(productId);

            log.debug("Found {} movements for productId={}", stockList.size(), productId);

            for (StockMovement stockMovement : stockList) {
                StockMovementResponse response = new StockMovementResponse();

                response.setOrderId(stockMovement.getOrderId());
                response.setProductId(stockMovement.getProductId());
                response.setQuantity(stockMovement.getQuantity());
                response.setReason(stockMovement.getReason());
                response.setType(stockMovement.getType());

                list.add(response);
            }

            log.info("Successfully fetched stock movements for productId={}", productId);

            return list;

        } catch (Exception e) {
            log.error("Failed to fetch stock movements for productId={}", productId, e);
            throw new StockMovementException("Failed to fetch stock movements", e);
        }
    }
}