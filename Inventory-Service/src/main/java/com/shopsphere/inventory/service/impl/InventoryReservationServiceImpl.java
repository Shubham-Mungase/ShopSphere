package com.shopsphere.inventory.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.inventory.dto.event.DomainEvent;
import com.shopsphere.inventory.dto.event.InventoryReservationFailedEvent;
import com.shopsphere.inventory.dto.event.InventoryReservedEvent;
import com.shopsphere.inventory.dto.event.StockConfirmedEvent;
import com.shopsphere.inventory.dto.event.StockReleasedEvent;
import com.shopsphere.inventory.dto.request.ConfirmStockRequest;
import com.shopsphere.inventory.dto.request.ReleaseStockRequest;
import com.shopsphere.inventory.dto.request.ReserveStockItem;
import com.shopsphere.inventory.dto.request.ReserveStockRequest;
import com.shopsphere.inventory.entity.Inventory;
import com.shopsphere.inventory.entity.InventoryReservation;
import com.shopsphere.inventory.entity.OutboxEntity;
import com.shopsphere.inventory.enums.InventoryEventType;
import com.shopsphere.inventory.enums.MovementType;
import com.shopsphere.inventory.enums.OutboxStatus;
import com.shopsphere.inventory.enums.ReservationStatus;
import com.shopsphere.inventory.repo.InventoryRepo;
import com.shopsphere.inventory.repo.InventoryReservationRepo;
import com.shopsphere.inventory.repo.OutboxRepo;
import com.shopsphere.inventory.service.InventoryReservationService;
import com.shopsphere.inventory.service.StockMovementService;

@Service
public class InventoryReservationServiceImpl implements InventoryReservationService {

	private final InventoryRepo inventoryRepo;
	private final InventoryReservationRepo reservationRepo;
	private final StockMovementService movementService;
	private final ObjectMapper mapper;
	private final OutboxRepo outboxRepo;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public InventoryReservationServiceImpl(InventoryRepo inventoryRepo, InventoryReservationRepo reservationRepo,
			StockMovementService movementService, ObjectMapper mapper, OutboxRepo outboxRepo,
			KafkaTemplate<String, Object> kafkaTemplate) {
		super();
		this.inventoryRepo = inventoryRepo;
		this.reservationRepo = reservationRepo;
		this.movementService = movementService;
		this.mapper = mapper;
		this.outboxRepo = outboxRepo;
		this.kafkaTemplate = kafkaTemplate;
	}

	// ==================================================
	// 1RESERVE STOCK
	// ==================================================

	@Override
	@Transactional
	public void reserveStock(ReserveStockRequest request) {

		List<Inventory> updatedInventories = new ArrayList<>();
		List<InventoryReservation> reservations = new ArrayList<>();
		List<UUID> reservedProductIds = new ArrayList<>();
		List<UUID> failedProductIds = new ArrayList<>();

		for (ReserveStockItem item : request.getRequests()) {

			// 1️⃣ Idempotency check
			if (reservationRepo.existsByOrderIdAndProductId(request.getOrderId(), item.getProductId())) {
				continue;
			}

			// 2️⃣ Lock inventory
			Inventory inventory = inventoryRepo.findByProductIdForUpdate(item.getProductId())
					.orElseThrow(() -> new RuntimeException("Inventory Not Found for product: " + item.getProductId()));

			// 3️⃣ Validate stock
			if (inventory.getAvailableStock() < item.getQuantity()) {
				failedProductIds.add(item.getProductId());
				continue;
			}

			// 4️⃣ Update inventory
			inventory.setAvailableStock(inventory.getAvailableStock() - item.getQuantity());

			inventory.setReservedStock(inventory.getReservedStock() + item.getQuantity());

			inventory.updateStatus();
			updatedInventories.add(inventory);

			// 5️⃣ Create reservation
			InventoryReservation reservation = new InventoryReservation();
			reservation.setOrderId(request.getOrderId());
			reservation.setProductId(item.getProductId());
			reservation.setQuantity(item.getQuantity());
			reservation.setStatus(ReservationStatus.RESERVED);

			reservations.add(reservation);

			reservedProductIds.add(item.getProductId());

			// 6️⃣ Record movement
			movementService.recordMovement(item.getProductId(), request.getOrderId(), MovementType.RESERVE,
					item.getQuantity(), "Stock reserved for order " + request.getOrderId());
		} // loop complete

		// 🚨 If ANY product failed → whole order fails
		if (!failedProductIds.isEmpty()) {

			throw new RuntimeException("Insufficient stock for products: " + failedProductIds);
		}

		// 7️⃣ Save DB changes
		inventoryRepo.saveAll(updatedInventories);
		reservationRepo.saveAll(reservations);

		
		
		InventoryReservedEvent reserveEvent = new InventoryReservedEvent(request.getOrderId(),request.getUserId(),request.getTotalAmount(), reservedProductIds,
				ReservationStatus.RESERVED.name());

		String eventId = UUID.randomUUID().toString();

		DomainEvent<InventoryReservedEvent> event = new DomainEvent<>(eventId,
				InventoryEventType.INVENTORY_RESERVED.name(), LocalDateTime.now(), reserveEvent);

		String payload;
		try {
			payload = mapper.writeValueAsString(event);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to serialize event", e);
		}

		OutboxEntity entity = new OutboxEntity(eventId, "INVENTORY", request.getOrderId().toString(),

				InventoryEventType.INVENTORY_RESERVED.name(), payload, OutboxStatus.NEW);

		outboxRepo.save(entity);

	}

	// ==================================================
	// 2️CONFIRM STOCK (Payment Success)
	// ==================================================
	@Override
	@Transactional
	public void confirmStock(ConfirmStockRequest request) {

		List<Inventory> updatedInventories = new ArrayList<>();
		List<InventoryReservation> updatedReservations = new ArrayList<>();
		List<UUID> confirmedProductIds = new ArrayList<>();

		for (UUID productId : request.getProductIds()) {

			InventoryReservation reservation = reservationRepo
					.findByOrderIdAndProductId(request.getOrderId(), productId)
					.orElseThrow(() -> new RuntimeException("Reservation not found for product: " + productId));

			// ✅ Idempotency
			if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
				continue;
			}

			// Lock inventory
			Inventory inventory = inventoryRepo.findByProductIdForUpdate(productId)
					.orElseThrow(() -> new RuntimeException("Inventory Not Found for product: " + productId));

			// 🚨 Safety check
			if (inventory.getReservedStock() < reservation.getQuantity()) {
				throw new RuntimeException("Reserved stock mismatch for product: " + productId);
			}

			// Reduce reserved stock permanently
			inventory.setReservedStock(inventory.getReservedStock() - reservation.getQuantity());

			inventory.updateStatus();
			reservation.setStatus(ReservationStatus.CONFIRMED);

			updatedInventories.add(inventory);
			updatedReservations.add(reservation);
			confirmedProductIds.add(productId);

			movementService.recordMovement(productId, request.getOrderId(), MovementType.CONFIRM,
					reservation.getQuantity(), "Stock confirmed after payment for order " + request.getOrderId());
		}

		inventoryRepo.saveAll(updatedInventories);
		reservationRepo.saveAll(updatedReservations);

		// ✅ Publish Confirmed Event

		StockConfirmedEvent confirmedEvent = new StockConfirmedEvent(request.getOrderId(), confirmedProductIds);

		String eventId = UUID.randomUUID().toString();

		DomainEvent<StockConfirmedEvent> event = new DomainEvent<StockConfirmedEvent>(eventId,
				InventoryEventType.INVENTORY_CONFIRMED.toString(), LocalDateTime.now(), confirmedEvent);

		String paylod = null;
		try {
			paylod = mapper.writeValueAsString(event);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		OutboxEntity entity = new OutboxEntity(eventId, "INVENTORY", request.getOrderId().toString(),
				InventoryEventType.INVENTORY_CONFIRMED.name(), paylod, OutboxStatus.NEW);

		outboxRepo.save(entity);

	}

	// ==================================================
	// 3️ RELEASE STOCK (Payment Failed)
	// ==================================================
	@Override
	@Transactional
	public void releaseStock(ReleaseStockRequest request) {

		List<Inventory> updatedInventories = new ArrayList<>();
		List<InventoryReservation> updatedReservations = new ArrayList<>();
		List<UUID> releasedProductIds = new ArrayList<>();

		
			for (UUID productId : request.getProductIds()) {

				InventoryReservation reservation = reservationRepo
						.findByOrderIdAndProductId(request.getOrderId(), productId)
						.orElseThrow(() -> new RuntimeException("Reservation not found for product: " + productId));

				// ✅ Idempotency
				if (reservation.getStatus() == ReservationStatus.RELEASED) {
					continue;
				}

				Inventory inventory = inventoryRepo.findByProductIdForUpdate(productId)
						.orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));

				// 🚨 Safety check
				if (inventory.getReservedStock() < reservation.getQuantity()) {
					throw new RuntimeException("Reserved stock mismatch for product: " + productId);
				}

				// Return stock to available
				inventory.setAvailableStock(inventory.getAvailableStock() + reservation.getQuantity());

				inventory.setReservedStock(inventory.getReservedStock() - reservation.getQuantity());

				inventory.updateStatus();
				reservation.setStatus(ReservationStatus.RELEASED);

				updatedInventories.add(inventory);
				updatedReservations.add(reservation);
				releasedProductIds.add(productId);

				movementService.recordMovement(productId, request.getOrderId(), MovementType.RELEASE,
						reservation.getQuantity(),
						"Stock released due to payment failure for order " + request.getOrderId());
			}

			inventoryRepo.saveAll(updatedInventories);
			reservationRepo.saveAll(updatedReservations);

			// ✅ Publish Released Event
			StockReleasedEvent releasedEvent = new StockReleasedEvent(request.getOrderId(), releasedProductIds);

			String eventId=UUID.randomUUID().toString();
			DomainEvent<StockReleasedEvent> event=new DomainEvent<StockReleasedEvent>(eventId,	InventoryEventType.INVENTORY_RELEASED.toString(), LocalDateTime.now(), releasedEvent);
			
			String paylod=null;
			try {
				paylod = mapper.writeValueAsString(event);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OutboxEntity entity = new OutboxEntity(eventId, "INVENTORY", request.getOrderId().toString(),
					InventoryEventType.INVENTORY_RELEASED.name(), paylod, OutboxStatus.NEW);
			outboxRepo.save(entity);
		
	}
}
