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
import com.shopsphere.inventory.dto.event.*;
import com.shopsphere.inventory.dto.request.*;
import com.shopsphere.inventory.entity.Inventory;
import com.shopsphere.inventory.entity.InventoryReservation;
import com.shopsphere.inventory.exception.EventDeserializationException;
import com.shopsphere.inventory.exception.InvalidEventException;
import com.shopsphere.inventory.exception.InventoryProcessingException;
import com.shopsphere.inventory.repo.InventoryRepo;
import com.shopsphere.inventory.repo.InventoryReservationRepo;
import com.shopsphere.inventory.service.InventoryReservationService;

@Component
public class InventoryKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(InventoryKafkaListener.class);

    private final InventoryRepo inventoryRepo;
    private final InventoryReservationService reservationService;
    private final ObjectMapper objectMapper;
    private final InventoryReservationRepo reservationRepo;

    public InventoryKafkaListener(InventoryRepo inventoryRepo,
                                  InventoryReservationService reservationService,
                                  ObjectMapper objectMapper,
                                  InventoryReservationRepo reservationRepo) {
        this.inventoryRepo = inventoryRepo;
        this.reservationService = reservationService;
        this.objectMapper = objectMapper;
        this.reservationRepo = reservationRepo;
    }

    // ================= PRODUCT =================

    @KafkaListener(
            topics = "product-events",
            groupId = AppConstants.GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleProductCreated(String message) {

        log.info("Received product event");

        ProductCreatedEvent createdEvent;

        try {
            createdEvent = objectMapper.readValue(message, ProductCreatedEvent.class);
        } catch (Exception e) {
            log.error("Failed to deserialize ProductCreatedEvent payload={}", message, e);
            throw new EventDeserializationException("Invalid product event payload", e);
        }

        if (createdEvent == null) {
            log.error("ProductCreatedEvent is null");
            throw new InvalidEventException("Product event is null");
        }

        if (!"PRODUCT_CREATED".equalsIgnoreCase(createdEvent.getEventType())) {
            log.debug("Ignoring non PRODUCT_CREATED event");
            return;
        }

        if (inventoryRepo.existsByProductId(createdEvent.getProductId())) {
            log.debug("Inventory already exists for productId={}", createdEvent.getProductId());
            return;
        }

        Inventory inventory = new Inventory();
        inventory.setProductId(createdEvent.getProductId());
        inventory.setAvailableStock(0);
        inventory.setReservedStock(0);
        inventory.setLowStockThreshold(5);
        inventory.updateStatus();
        inventory.setProductName(createdEvent.getProductName());
        inventory.setWarehouse(null);
        inventoryRepo.save(inventory);

        log.info("Inventory created for productId={}", createdEvent.getProductId());
    }

    // ================= ORDER =================

    @KafkaListener(
            topics = "order-events",
            groupId = AppConstants.GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderCreated(String message) {

        log.info("Received order event :"+message);

        try {

            OrderCreatedEvent event =
                    objectMapper.readValue(message, OrderCreatedEvent.class);

            if (event == null) {
                throw new InvalidEventException("OrderCreatedEvent is null");
            }

            log.info("Processing orderId={}", event.getOrderId());

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

            log.info("Stock reserved successfully for orderId={}", event.getOrderId());

        } catch (InvalidEventException e) {
            log.error("Invalid order event", e);
            throw e;
        } catch (Exception ex) {
            log.error("Inventory processing failed for message={}", message, ex);
            throw new InventoryProcessingException("Inventory processing failed", ex);
        }
    }

    // ================= PAYMENT =================

    @KafkaListener(
            topics = "payment-events",
            groupId = AppConstants.GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void handlePaymentEvent(String message) {

        log.info("Received payment event");

        try {

            JsonNode rootNode = objectMapper.readTree(message);
            String eventType = rootNode.get("eventType").asText();
            JsonNode payloadNode = rootNode.get("payload");

            switch (eventType) {

                case "PAYMENT_SUCCESS" -> {

                    log.info("Processing PAYMENT_SUCCESS");

                    PaymentSuccessEvent event =
                            objectMapper.treeToValue(payloadNode, PaymentSuccessEvent.class);

                    List<InventoryReservation> reservations =
                            reservationRepo.findByOrderId(event.getOrderId());

                    if (reservations.isEmpty()) {
                        log.warn("No reservations found for orderId={}", event.getOrderId());
                        return;
                    }

                    List<UUID> productIds = reservations.stream()
                            .map(InventoryReservation::getProductId)
                            .toList();

                    ConfirmStockRequest request = new ConfirmStockRequest();
                    request.setOrderId(event.getOrderId());
                    request.setProductIds(productIds);

                    reservationService.confirmStock(request);

                    log.info("Stock confirmed for orderId={}", event.getOrderId());
                }

                case "PAYMENT_FAILED" -> {

                    log.info("Processing PAYMENT_FAILED");

                    PaymentFailedEvent event =
                            objectMapper.treeToValue(payloadNode, PaymentFailedEvent.class);

                    List<InventoryReservation> reservations =
                            reservationRepo.findByOrderId(event.getOrderId());

                    if (reservations.isEmpty()) {
                        log.warn("No reservations found for orderId={}", event.getOrderId());
                        return;
                    }

                    List<UUID> productIds = reservations.stream()
                            .map(InventoryReservation::getProductId)
                            .toList();

                    ReleaseStockRequest request = new ReleaseStockRequest();
                    request.setOrderId(event.getOrderId());
                    request.setProductIds(productIds);

                    reservationService.releaseStock(request);

                    log.info("Stock released for orderId={}", event.getOrderId());
                }

                default -> {
                    log.warn("Unknown payment eventType={}", eventType);
                    throw new InvalidEventException("Unknown payment event: " + eventType);
                }
            }

        } catch (InvalidEventException e) {
            log.error("Invalid payment event", e);
            throw e;
        } catch (Exception e) {
            log.error("Error processing payment event payload={}", message, e);
            throw new InventoryProcessingException("Payment event processing failed", e);
        }
    }
}