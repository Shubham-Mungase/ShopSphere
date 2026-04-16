package com.shopsphere.inventory.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.inventory.dto.event.DomainEvent;
import com.shopsphere.inventory.dto.event.InventoryReservedEvent;
import com.shopsphere.inventory.dto.event.StockConfirmedEvent;
import com.shopsphere.inventory.dto.event.StockReleasedEvent;
import com.shopsphere.inventory.dto.request.*;
import com.shopsphere.inventory.entity.*;
import com.shopsphere.inventory.enums.*;
import com.shopsphere.inventory.exception.*;
import com.shopsphere.inventory.repo.*;
import com.shopsphere.inventory.service.*;

@Service
public class InventoryReservationServiceImpl implements InventoryReservationService {

    private static final Logger log = LoggerFactory.getLogger(InventoryReservationServiceImpl.class);

    private final InventoryRepo inventoryRepo;
    private final InventoryReservationRepo reservationRepo;
    private final StockMovementService movementService;
    private final ObjectMapper mapper;
    private final OutboxRepo outboxRepo;

    public InventoryReservationServiceImpl(InventoryRepo inventoryRepo,
                                           InventoryReservationRepo reservationRepo,
                                           StockMovementService movementService,
                                           ObjectMapper mapper,
                                           OutboxRepo outboxRepo
                                          ) {
        this.inventoryRepo = inventoryRepo;
        this.reservationRepo = reservationRepo;
        this.movementService = movementService;
        this.mapper = mapper;
        this.outboxRepo = outboxRepo;
    }

    // ========================= RESERVE STOCK =========================
    @Override
    @Transactional
    public void reserveStock(ReserveStockRequest request) {

        log.info("Starting stock reservation for orderId={}", request.getOrderId());

        List<Inventory> updatedInventories = new ArrayList<>();
        List<InventoryReservation> reservations = new ArrayList<>();
        List<UUID> reservedProductIds = new ArrayList<>();
        List<UUID> failedProductIds = new ArrayList<>();

        for (ReserveStockItem item : request.getRequests()) {

            if (reservationRepo.existsByOrderIdAndProductId(request.getOrderId(), item.getProductId())) {
                log.debug("Skipping already reserved productId={}", item.getProductId());
                continue;
            }

            Inventory inventory = inventoryRepo.findByProductIdForUpdate(item.getProductId())
                    .orElseThrow(() -> {
                        log.error("Inventory not found for productId={}", item.getProductId());
                        return new InventoryNotFoundException("Inventory Not Found: " + item.getProductId());
                    });

            if (inventory.getAvailableStock() < item.getQuantity()) {
                log.warn("Insufficient stock for productId={}", item.getProductId());
                failedProductIds.add(item.getProductId());
                continue;
            }

            inventory.setAvailableStock(inventory.getAvailableStock() - item.getQuantity());
            inventory.setReservedStock(inventory.getReservedStock() + item.getQuantity());
            inventory.updateStatus();

            updatedInventories.add(inventory);

            InventoryReservation reservation = new InventoryReservation();
            reservation.setOrderId(request.getOrderId());
            reservation.setProductId(item.getProductId());
            reservation.setQuantity(item.getQuantity());
            reservation.setStatus(ReservationStatus.RESERVED);

            reservations.add(reservation);
            reservedProductIds.add(item.getProductId());

            movementService.recordMovement(item.getProductId(), request.getOrderId(),
                    MovementType.RESERVE, item.getQuantity(),
                    "Stock reserved for order " + request.getOrderId());
        }

        if (!failedProductIds.isEmpty()) {
            log.error("Reservation failed for products={}", failedProductIds);
            throw new InsufficientStockException("Insufficient stock: " + failedProductIds);
        }

        inventoryRepo.saveAll(updatedInventories);
        reservationRepo.saveAll(reservations);

        log.info("Stock reserved successfully for orderId={}", request.getOrderId());

        InventoryReservedEvent reserveEvent = new InventoryReservedEvent(
                request.getOrderId(),
                request.getUserId(),
                request.getTotalAmount(),
                reservedProductIds,
                ReservationStatus.RESERVED.name()
        );

        String eventId = UUID.randomUUID().toString();

        DomainEvent<InventoryReservedEvent> event = new DomainEvent<>(
                eventId,
                InventoryEventType.INVENTORY_RESERVED.name(),
                LocalDateTime.now(),
                reserveEvent
        );

        try {
            String payload = mapper.writeValueAsString(event);

            outboxRepo.save(new OutboxEntity(
                    eventId,
                    "INVENTORY",
                    request.getOrderId().toString(),
                    InventoryEventType.INVENTORY_RESERVED.name(),
                    payload,
                    OutboxStatus.NEW
            ));

            log.info("InventoryReservedEvent saved for orderId={}", request.getOrderId());

        } catch (JsonProcessingException e) {
            log.error("Serialization failed for orderId={}", request.getOrderId(), e);
            throw new EventSerializationException("Serialization failed", e);
        }
    }

    // ========================= CONFIRM STOCK =========================
    @Override
    @Transactional
    public void confirmStock(ConfirmStockRequest request) {

        log.info("Confirming stock for orderId={}", request.getOrderId());

        List<Inventory> updatedInventories = new ArrayList<>();
        List<InventoryReservation> updatedReservations = new ArrayList<>();
        List<UUID> confirmedProductIds = new ArrayList<>();

        for (UUID productId : request.getProductIds()) {

            InventoryReservation reservation = reservationRepo
                    .findByOrderIdAndProductId(request.getOrderId(), productId)
                    .orElseThrow(() -> {
                        log.error("Reservation not found for productId={}", productId);
                        return new ReservationNotFoundException("Reservation not found: " + productId);
                    });

            if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                log.debug("Already confirmed productId={}", productId);
                continue;
            }

            Inventory inventory = inventoryRepo.findByProductIdForUpdate(productId)
                    .orElseThrow(() -> {
                        log.error("Inventory not found for productId={}", productId);
                        return new InventoryNotFoundException("Inventory not found: " + productId);
                    });

            if (inventory.getReservedStock() < reservation.getQuantity()) {
                log.error("Stock mismatch for productId={}", productId);
                throw new StockMismatchException("Stock mismatch: " + productId);
            }

            inventory.setReservedStock(inventory.getReservedStock() - reservation.getQuantity());
            inventory.updateStatus();
            reservation.setStatus(ReservationStatus.CONFIRMED);

            updatedInventories.add(inventory);
            updatedReservations.add(reservation);
            confirmedProductIds.add(productId);

            movementService.recordMovement(productId, request.getOrderId(),
                    MovementType.CONFIRM, reservation.getQuantity(),
                    "Stock confirmed");
        }

        inventoryRepo.saveAll(updatedInventories);
        reservationRepo.saveAll(updatedReservations);

        try {
            String eventId = UUID.randomUUID().toString();

            String payload = mapper.writeValueAsString(new DomainEvent<>(
                    eventId,
                    InventoryEventType.INVENTORY_CONFIRMED.name(),
                    LocalDateTime.now(),
                    new StockConfirmedEvent(request.getOrderId(), confirmedProductIds)
            ));

            outboxRepo.save(new OutboxEntity(
                    eventId,
                    "INVENTORY",
                    request.getOrderId().toString(),
                    InventoryEventType.INVENTORY_CONFIRMED.name(),
                    payload,
                    OutboxStatus.NEW
            ));

            log.info("StockConfirmedEvent saved for orderId={}", request.getOrderId());

        } catch (Exception e) {
            log.error("Failed to create confirmed event for orderId={}", request.getOrderId(), e);
            throw new EventSerializationException("Failed to create event", e);
        }
    }

    // ========================= RELEASE STOCK =========================
    @Override
    @Transactional
    public void releaseStock(ReleaseStockRequest request) {

        log.info("Releasing stock for orderId={}", request.getOrderId());

        List<Inventory> updatedInventories = new ArrayList<>();
        List<InventoryReservation> updatedReservations = new ArrayList<>();
        List<UUID> releasedProductIds = new ArrayList<>();

        for (UUID productId : request.getProductIds()) {

            InventoryReservation reservation = reservationRepo
                    .findByOrderIdAndProductId(request.getOrderId(), productId)
                    .orElseThrow(() -> {
                        log.error("Reservation not found for productId={}", productId);
                        return new ReservationNotFoundException("Reservation not found: " + productId);
                    });

            if (reservation.getStatus() == ReservationStatus.RELEASED) {
                log.debug("Already released productId={}", productId);
                continue;
            }

            Inventory inventory = inventoryRepo.findByProductIdForUpdate(productId)
                    .orElseThrow(() -> {
                        log.error("Inventory not found for productId={}", productId);
                        return new InventoryNotFoundException("Inventory not found: " + productId);
                    });

            if (inventory.getReservedStock() < reservation.getQuantity()) {
                log.error("Stock mismatch for productId={}", productId);
                throw new StockMismatchException("Stock mismatch: " + productId);
            }

            inventory.setAvailableStock(inventory.getAvailableStock() + reservation.getQuantity());
            inventory.setReservedStock(inventory.getReservedStock() - reservation.getQuantity());
            inventory.updateStatus();

            reservation.setStatus(ReservationStatus.RELEASED);

            updatedInventories.add(inventory);
            updatedReservations.add(reservation);
            releasedProductIds.add(productId);

            movementService.recordMovement(productId, request.getOrderId(),
                    MovementType.RELEASE, reservation.getQuantity(),
                    "Stock released");
        }

        inventoryRepo.saveAll(updatedInventories);
        reservationRepo.saveAll(updatedReservations);

        try {
            String eventId = UUID.randomUUID().toString();

            String payload = mapper.writeValueAsString(new DomainEvent<>(
                    eventId,
                    InventoryEventType.INVENTORY_RELEASED.name(),
                    LocalDateTime.now(),
                    new StockReleasedEvent(request.getOrderId(), releasedProductIds)
            ));

            outboxRepo.save(new OutboxEntity(
                    eventId,
                    "INVENTORY",
                    request.getOrderId().toString(),
                    InventoryEventType.INVENTORY_RELEASED.name(),
                    payload,
                    OutboxStatus.NEW
            ));

            log.info("StockReleasedEvent saved for orderId={}", request.getOrderId());

        } catch (Exception e) {
            log.error("Failed to create release event for orderId={}", request.getOrderId(), e);
            throw new EventSerializationException("Failed to create event", e);
        }
    }
}