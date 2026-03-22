package com.shopsphere.shipping.service.impl;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.shipping.client.InventoryClient;
import com.shopsphere.shipping.dto.event.ShipmentEventDTO;
import com.shopsphere.shipping.dto.response.ShipmentResponse;
import com.shopsphere.shipping.dto.response.ShipmentTrackingResponse;
import com.shopsphere.shipping.dto.response.WarehouseResponse;
import com.shopsphere.shipping.entity.Shipment;
import com.shopsphere.shipping.entity.ShipmentTracking;
import com.shopsphere.shipping.enums.ShipmentStatus;
import com.shopsphere.shipping.repo.ShipmentRepository;
import com.shopsphere.shipping.repo.ShipmentTrackingRepository;
import com.shopsphere.shipping.service.ShipmentService;

@Service
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepo;
    private final ShipmentTrackingRepository trackingRepo;
    private final InventoryClient client;

  
    public ShipmentServiceImpl(ShipmentRepository shipmentRepo, ShipmentTrackingRepository trackingRepo,
			InventoryClient client) {
		super();
		this.shipmentRepo = shipmentRepo;
		this.trackingRepo = trackingRepo;
		this.client = client;
	}

    @Override
    public ShipmentEventDTO createShipment(UUID orderId, UUID productId) {

        // 1️⃣ Call Inventory Service to get warehouse
    	WarehouseResponse warehouse =null;
    	  Shipment shipment = new Shipment();
    	try {

    	    
    	           warehouse= client.getWarehouse(productId);

    	    shipment.setWarehouseId(warehouse.getWarehouseId());

    	} catch (Exception ex) {

    	    shipment.setStatus(ShipmentStatus.PENDING);

    	}
        // 2️⃣ Create Shipment
       

        shipment.setOrderId(orderId);
        shipment.setTrackingNumber(generateTrackingNumber());
        shipment.setCourierPartner(assignCourier());
        shipment.setStatus(ShipmentStatus.CREATED);
        shipment.setCreatedAt(LocalDateTime.now());
        shipment.setUpdatedAt(LocalDateTime.now());

        Shipment savedShipment = shipmentRepo.save(shipment);

        // 3️⃣ Create Tracking Entry
        createTrackingEntry(
                savedShipment,
                ShipmentStatus.CREATED,
                warehouse.getLocation()   // example: Mumbai Warehouse
        );

        // 4️⃣ Return Event
        return mapEvent(savedShipment);
    }
    @Override
    public ShipmentResponse getShipmentByOrderId(UUID orderId) {

        Shipment shipment = shipmentRepo.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        return mapResponse(shipment);
    }

    @Override
    public List<ShipmentTrackingResponse> getTrackingHistory(String trackingNumber) {

        Shipment shipment = shipmentRepo.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        List<ShipmentTracking> trackingList =
                trackingRepo.findByShipmentIdOrderByTimestampAsc(shipment.getShipmentId());

        return trackingList.stream()
                .map(this::mapTracking)
                .collect(Collectors.toList());
    }

    @Override
    public void updateShipmentStatus(UUID shipmentId, String status, String location) {

        Shipment shipment = shipmentRepo.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        ShipmentStatus shipmentStatus = ShipmentStatus.valueOf(status);

        shipment.setStatus(shipmentStatus);
        shipment.setUpdatedAt(LocalDateTime.now());

        shipmentRepo.save(shipment);

        createTrackingEntry(shipment, shipmentStatus, location);
    }

    private void createTrackingEntry(Shipment shipment,
                                     ShipmentStatus status,
                                     String location) {

        ShipmentTracking tracking = new ShipmentTracking();

        tracking.setShipmentId(shipment.getShipmentId());
        tracking.setStatus(status);
        tracking.setLocation(location);
        tracking.setTimestamp(LocalDateTime.now());

        trackingRepo.save(tracking);
    }

    private String generateTrackingNumber() {

        return "TRK-" + UUID.randomUUID().toString().substring(0, 8);
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
}