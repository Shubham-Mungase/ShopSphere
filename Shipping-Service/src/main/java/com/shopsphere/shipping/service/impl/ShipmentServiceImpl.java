package com.shopsphere.shipping.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.shipping.client.InventoryClient;
import com.shopsphere.shipping.dto.event.OrderItemDTO;
import com.shopsphere.shipping.dto.event.ShipmentEventDTO;
import com.shopsphere.shipping.dto.response.ShipmentResponse;
import com.shopsphere.shipping.dto.response.ShipmentTrackingResponse;
import com.shopsphere.shipping.dto.response.WarehouseResponse;
import com.shopsphere.shipping.entity.OutboxEvent;
import com.shopsphere.shipping.entity.Shipment;
import com.shopsphere.shipping.entity.ShipmentAddress;
import com.shopsphere.shipping.entity.ShipmentItem;
import com.shopsphere.shipping.entity.ShipmentTracking;
import com.shopsphere.shipping.enums.ShipmentStatus;
import com.shopsphere.shipping.exceptions.InventoryServiceException;
import com.shopsphere.shipping.exceptions.ShipmentNotFoundException;
import com.shopsphere.shipping.exceptions.ShippmentAlreadyExist;
import com.shopsphere.shipping.repo.OutboxRepository;
import com.shopsphere.shipping.repo.ShipmentRepository;
import com.shopsphere.shipping.repo.ShipmentTrackingRepository;
import com.shopsphere.shipping.service.ShipmentService;

@Service
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

	private static final Logger log = LoggerFactory.getLogger(ShipmentServiceImpl.class);

	private final ShipmentRepository shipmentRepo;
	private final ShipmentTrackingRepository trackingRepo;
	private final InventoryClient inventoryClient;
	private final OutboxRepository outboxRepository;
	private final ObjectMapper objectMapper;

	public ShipmentServiceImpl(ShipmentRepository shipmentRepo, ShipmentTrackingRepository trackingRepo,
			InventoryClient inventoryClient, OutboxRepository outboxRepository, ObjectMapper objectMapper) {
		this.shipmentRepo = shipmentRepo;
		this.trackingRepo = trackingRepo;
		this.inventoryClient = inventoryClient;
		this.outboxRepository = outboxRepository;
		this.objectMapper = objectMapper;
	}

	// =========================================================
	// CREATE SHIPMENT
	// =========================================================
	@Override
	public List<ShipmentEventDTO> createShipment(
	        UUID orderId,
	        List<OrderItemDTO> items,
	        ShipmentAddress address) {

	    log.info("Creating shipments for orderId={}", orderId);

	    //  Idempotency
	    if (shipmentRepo.existsByOrderId(orderId)) {
	        log.warn("Shipment already exists for orderId={}", orderId);
	        throw new ShippmentAlreadyExist("Shipment already created");
	    }

	    List<ShipmentEventDTO> events = new ArrayList<>();

	    // =========================================================
	    //  STEP 1: Extract UNIQUE productIds
	    // =========================================================
	    List<UUID> productIds = items.stream()
	            .map(OrderItemDTO::getProductId)
	            .distinct()
	            .toList();

	    // =========================================================
	    //  STEP 2: BULK CALL (ONLY ONE API CALL)
	    // =========================================================
	    Map<UUID, UUID> productWarehouseMap =
	            inventoryClient.getWarehousesForProducts(productIds);

	    // Optional safety check
	    if (productWarehouseMap.size() != productIds.size()) {
	        throw new InventoryServiceException("Some products missing in inventory", null);
	    }

	    // =========================================================
	    //  STEP 3: GROUP ITEMS BY WAREHOUSE
	    // =========================================================
	    Map<UUID, List<OrderItemDTO>> warehouseMap = new HashMap<>();

	    for (OrderItemDTO item : items) {

	        UUID warehouseId = productWarehouseMap.get(item.getProductId());

	        if (warehouseId == null) {
	            throw new InventoryServiceException(
	                    "No warehouse found for productId=" + item.getProductId(), null);
	        }

	        warehouseMap
	                .computeIfAbsent(warehouseId, k -> new ArrayList<>())
	                .add(item);
	    }

	    // =========================================================
	    //  STEP 4: CREATE SHIPMENT PER WAREHOUSE
	    // =========================================================
	    for (Map.Entry<UUID, List<OrderItemDTO>> entry : warehouseMap.entrySet()) {

	        UUID warehouseId = entry.getKey();
	        List<OrderItemDTO> warehouseItems = entry.getValue();

	        Shipment shipment = new Shipment();
	        shipment.setOrderId(orderId);
	        shipment.setWarehouseId(warehouseId);
	        shipment.setTrackingNumber(generateTrackingNumber());
	        shipment.setCourierPartner(assignCourier());
	        shipment.setStatus(ShipmentStatus.CREATED);
	        shipment.setCreatedAt(LocalDateTime.now());
	        shipment.setUpdatedAt(LocalDateTime.now());

	        //  Address (same for all shipments)
	        shipment.setShipmentAddress(address);

	        // =====================================================
	        //  STEP 5: MAP ITEMS
	        // =====================================================
	        List<ShipmentItem> shipmentItems = new ArrayList<>();

	        for (OrderItemDTO item : warehouseItems) {

	            ShipmentItem si = new ShipmentItem();
	            si.setProductId(item.getProductId());
	            si.setQuantity(item.getQuantity());
	            si.setShipment(shipment);

	            shipmentItems.add(si);
	        }

	        shipment.setItems(shipmentItems);

	        // =====================================================
	        //  SAVE
	        // =====================================================
	        Shipment savedShipment = shipmentRepo.save(shipment);

	        // =====================================================
	        //  TRACKING
	        // =====================================================
	        createTrackingEntry(
	                savedShipment,
	                ShipmentStatus.CREATED,
	                "WAREHOUSE-" + warehouseId
	        );

	        // =====================================================
	        // EVENT + OUTBOX
	        // =====================================================
	        ShipmentEventDTO event = mapEvent(savedShipment);

	        saveOutboxEvent(
	                savedShipment.getShipmentId(),
	                "SHIPMENT_CREATED",
	                event
	        );

	        events.add(event);
	    }

	    log.info("Shipment creation completed orderId={} totalShipments={}",
	            orderId, events.size());

	    return events;
	}

	// =========================================================
	// UPDATE STATUS
	// =========================================================
	@Override
	public void updateShipmentStatus(UUID shipmentId, String status, String location) {

		Shipment shipment = shipmentRepo.findById(shipmentId)
				.orElseThrow(() -> new ShipmentNotFoundException("Shipment not found for shipmentId=" + shipmentId));

		ShipmentStatus newStatus;

		try {
			newStatus = ShipmentStatus.valueOf(status.toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Invalid shipment status: " + status);
		}

		// ✅ Validate transition
		if (!isValidTransition(shipment.getStatus(), newStatus)) {
			throw new IllegalStateException("Invalid status transition");
		}

		shipment.setStatus(newStatus);
		shipment.setUpdatedAt(LocalDateTime.now());

		shipmentRepo.save(shipment);

		createTrackingEntry(shipment, newStatus, location);

		// 🔥 Create event based on status
		ShipmentEventDTO event = mapEvent(shipment);

		if (newStatus == ShipmentStatus.PICKED_UP) {
			saveOutboxEvent(shipmentId, "SHIPMENT_SHIPPED", event);
		} else if (newStatus == ShipmentStatus.DELIVERED) {
			saveOutboxEvent(shipmentId, "SHIPMENT_DELIVERED", event);
		}

		log.info("Shipment updated shipmentId={} status={}", shipmentId, newStatus);
	}

	// =========================================================
	// READ APIs
	// =========================================================
	@Override
	@Transactional(readOnly = true)
	public List<ShipmentResponse> getShipmentsByOrderId(UUID orderId) {

		List<Shipment> shipments = shipmentRepo.findAllByOrderId(orderId);

		if (shipments.isEmpty()) {
			throw new ShipmentNotFoundException("No shipments found for orderId=" + orderId);
		}

		return shipments.stream().map(this::mapResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ShipmentTrackingResponse> getTrackingHistory(String trackingNumber) {

		Shipment shipment = shipmentRepo.findByTrackingNumber(trackingNumber).orElseThrow(
				() -> new ShipmentNotFoundException("Shipment not found for trackingNumber=" + trackingNumber));

		return trackingRepo.findByShipmentIdOrderByTimestampAsc(shipment.getShipmentId()).stream()
				.map(this::mapTracking).toList();
	}

	// =========================================================
	// OUTBOX METHOD
	// =========================================================
	private void saveOutboxEvent(UUID shipmentId, String eventType, ShipmentEventDTO event) {
		try {
			String payload = objectMapper.writeValueAsString(event);

			OutboxEvent outbox = new OutboxEvent("SHIPMENT", shipmentId, eventType, payload);

			outboxRepository.save(outbox);

			log.info("Outbox event saved type={} shipmentId={}", eventType, shipmentId);

		} catch (Exception e) {
			log.error("Failed to save outbox event shipmentId={}", shipmentId, e);
			throw new RuntimeException(e);
		}
	}

	// =========================================================
	// HELPERS
	// =========================================================
	private boolean isValidTransition(ShipmentStatus current, ShipmentStatus next) {
		return switch (current) {

		case CREATED -> next == ShipmentStatus.PICKED_UP;

		case PICKED_UP -> next == ShipmentStatus.IN_TRANSIT;

		case IN_TRANSIT -> next == ShipmentStatus.OUT_FOR_DELIVERY;

		case OUT_FOR_DELIVERY -> next == ShipmentStatus.DELIVERED;

		default -> false;
		};
	}

	private void createTrackingEntry(Shipment shipment, ShipmentStatus status, String location) {

		ShipmentTracking tracking = new ShipmentTracking();
		tracking.setShipmentId(shipment.getShipmentId());
		tracking.setStatus(status);
		tracking.setLocation(location != null ? location : "UNKNOWN");
		tracking.setTimestamp(LocalDateTime.now());

		trackingRepo.save(tracking);
	}

	private String generateTrackingNumber() {
		return "TRK-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
	}

	private String assignCourier() {
		return "DELHIVERY";
	}

	private ShipmentEventDTO mapEvent(Shipment shipment) {
		ShipmentEventDTO event = new ShipmentEventDTO();
		event.setShipmentId(shipment.getShipmentId());
		event.setOrderId(shipment.getOrderId());
		event.setTrackingNumber(shipment.getTrackingNumber());
		event.setStatus(shipment.getStatus().name());
		return event;
	}

	private ShipmentResponse mapResponse(Shipment shipment) {
		ShipmentResponse response = new ShipmentResponse();

		response.setShipmentId(shipment.getShipmentId());
		response.setOrderId(shipment.getOrderId());
		response.setProductId(shipment.getProductId());
		response.setQuantity(shipment.getQuantity());
		response.setWarehouseId(shipment.getWarehouseId());
		response.setCourierPartner(shipment.getCourierPartner());
		response.setTrackingNumber(shipment.getTrackingNumber());
		response.setStatus(shipment.getStatus().name());
		response.setCreatedAt(shipment.getCreatedAt());
		response.setUpdatedAt(shipment.getUpdatedAt());

		return response;
	}

	private ShipmentTrackingResponse mapTracking(ShipmentTracking tracking) {
		ShipmentTrackingResponse response = new ShipmentTrackingResponse();

		response.setShipmentId(tracking.getShipmentId());
		response.setStatus(tracking.getStatus().name());
		response.setLocation(tracking.getLocation());
		response.setTimestamp(tracking.getTimestamp());

		return response;
	}

	@Override
	public boolean isShipmentAlreadyCreated(UUID orderId) {

		if (orderId == null) {
			throw new IllegalArgumentException("OrderId cannot be null");
		}

		return shipmentRepo.existsByOrderId(orderId);
	}

}