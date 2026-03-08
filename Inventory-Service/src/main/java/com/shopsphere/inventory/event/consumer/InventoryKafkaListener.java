package com.shopsphere.inventory.event.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.inventory.constants.AppConstants;
import com.shopsphere.inventory.dto.event.OrderCreatedEvent;
import com.shopsphere.inventory.dto.event.OrderItemEvent;
import com.shopsphere.inventory.dto.event.PaymentFailedEvent;
import com.shopsphere.inventory.dto.event.PaymentSuccessEvent;
import com.shopsphere.inventory.dto.event.ProductCreatedEvent;
import com.shopsphere.inventory.dto.request.ConfirmStockRequest;
import com.shopsphere.inventory.dto.request.ReleaseStockRequest;
import com.shopsphere.inventory.dto.request.ReserveStockItem;
import com.shopsphere.inventory.dto.request.ReserveStockRequest;
import com.shopsphere.inventory.entity.Inventory;
import com.shopsphere.inventory.entity.InventoryReservation;
import com.shopsphere.inventory.repo.InventoryRepo;
import com.shopsphere.inventory.repo.InventoryReservationRepo;
import com.shopsphere.inventory.service.InventoryReservationService;

@Component
public class InventoryKafkaListener {

	private final InventoryRepo inventoryRepo;

	private final InventoryReservationService reservationService;

	
	private final ObjectMapper objectMapper;

	private final InventoryReservationRepo reservationRepo;
	

	private static final Logger log=LoggerFactory.getLogger(InventoryKafkaListener.class);

	public InventoryKafkaListener(InventoryRepo inventoryRepo, InventoryReservationService reservationService,
			 ObjectMapper objectMapper,
			InventoryReservationRepo reservationRepo) {
		super();
		this.inventoryRepo = inventoryRepo;
		this.reservationService = reservationService;
		this.objectMapper = objectMapper;
		this.reservationRepo = reservationRepo;
	}

	@KafkaListener(
	        topics = "product-events",
	        groupId = AppConstants.GROUP_ID,
	        containerFactory = "kafkaListenerContainerFactory"
	)
	public void handleProductCreated(String message) {

		log.info("Event received: {}", message);

	    ProductCreatedEvent createdEvent;

	    try {
	        createdEvent = objectMapper.readValue(message, ProductCreatedEvent.class);
	    } catch (Exception e) {
	        log.error("Failed to deserialize ProductCreatedEvent: " + e.getMessage());
	        return; // stop processing
	    }

	    if (createdEvent == null) {
	        System.err.println();
	        log.error("Received null ProductCreatedEvent");
		       
	        return;
	    }

	    // Validate event type first
	    if (!"PRODUCT_CREATED".equalsIgnoreCase(createdEvent.getEventType())) {
	        return; // Ignore other event types
	    }

	    // Check if inventory already exists
	    if (inventoryRepo.existsByProductId(createdEvent.getProductId())) {
	        return;
	    }

	    Inventory inventory = new Inventory();
	    inventory.setProductId(createdEvent.getProductId());
	    inventory.setAvailableStock(0);
	    inventory.setReservedStock(0);
	    inventory.setLowStockThreshold(5);
	    inventory.updateStatus();
	    inventory.setProductName(createdEvent.getProductName());

	    inventoryRepo.save(inventory);

	    System.out.println();
	    log.info("Inventory created for productId: " + createdEvent.getProductId());
	}

	@KafkaListener(
		    topics = "order-events",
		    groupId = AppConstants.GROUP_ID,
		    containerFactory = "kafkaListenerContainerFactory"
		)
		public void handleOrderCreated(String message) {

		    try {

		        OrderCreatedEvent event =
		                objectMapper.readValue(message, OrderCreatedEvent.class);

		        if (event == null) {
		            throw new RuntimeException("Received null OrderCreatedEvent");
		        }

		        System.out.println("Received OrderCreatedEvent: " + event.getOrderId());

		        ReserveStockRequest request = new ReserveStockRequest();

		        List<ReserveStockItem> items = new ArrayList<>();
		        for (OrderItemEvent item : event.getItems()) {

		            ReserveStockItem stockItem = new ReserveStockItem();
		            stockItem.setProductId(item.getProductId());
		            stockItem.setQuantity(item.getQuantity());

		            items.add(stockItem);
		        }

		        request.setOrderId(event.getOrderId());
		        request.setRequests(items);
		        request.setUserId(event.getUserId());
		        request.setTotalAmount(event.getTotalAmount());

		        reservationService.reserveStock(request);

		        System.out.println("Inventory reservation completed");

		    } catch (Exception ex) {

		        // VERY IMPORTANT → throw so Kafka retries
		        throw new RuntimeException("Inventory processing failed", ex);
		    }
		}
	
	@KafkaListener(
	        topics = "payment-events",
	        groupId = AppConstants.GROUP_ID,
	        containerFactory = "kafkaListenerContainerFactory"
	)
	@Transactional
	public void handlePaymentEvent(String message) {

		System.out.println("RAW MESSAGE: " + message);
	    try {

	        JsonNode rootNode = objectMapper.readTree(message);
	        String eventType = rootNode.get("eventType").asText();
	        JsonNode payloadNode = rootNode.get("payload");

	        switch (eventType) {

	            case "PAYMENT_SUCCESS" -> {

	                PaymentSuccessEvent event =
	                        objectMapper.treeToValue(payloadNode, PaymentSuccessEvent.class);

	                List<InventoryReservation> reservations =
	                        reservationRepo.findByOrderId(event.getOrderId());

	                if (reservations.isEmpty()) {
	                    log.warn("No reservations found for order: {}", event.getOrderId());
	                    return;
	                }

	                List<UUID> productIds = reservations.stream()
	                        .map(InventoryReservation::getProductId)
	                        .toList();

	                ConfirmStockRequest request = new ConfirmStockRequest();
	                request.setOrderId(event.getOrderId());
	                request.setProductIds(productIds);

	                reservationService.confirmStock(request);
	            }

	            case "PAYMENT_FAILED" -> {

	                PaymentFailedEvent event =
	                        objectMapper.treeToValue(payloadNode, PaymentFailedEvent.class);

	                List<InventoryReservation> reservations =
	                        reservationRepo.findByOrderId(event.getOrderId());

	                if (reservations.isEmpty()) {
	                    log.warn("No reservations found for order: {}", event.getOrderId());
	                    return;
	                }

	                List<UUID> productIds = reservations.stream()
	                        .map(InventoryReservation::getProductId)
	                        .toList();

	                ReleaseStockRequest request = new ReleaseStockRequest();
	                request.setOrderId(event.getOrderId());
	                request.setProductIds(productIds);

	                reservationService.releaseStock(request);
	            }

	            default -> log.warn("Unknown payment event type: {}", eventType);
	        }

	    } catch (Exception e) {
	        log.error("Error processing payment event", e);
	        throw new RuntimeException(e); // Kafka retry
	    }
	}
}