package com.shopsphere.shipping.service;

import java.util.List;
import java.util.UUID;

import com.shopsphere.shipping.dto.event.OrderItemDTO;
import com.shopsphere.shipping.dto.event.ShipmentEventDTO;
import com.shopsphere.shipping.dto.response.ShipmentResponse;
import com.shopsphere.shipping.dto.response.ShipmentTrackingResponse;
import com.shopsphere.shipping.entity.ShipmentAddress;

public interface ShipmentService {

	public List<ShipmentEventDTO> createShipment(UUID orderId, List<OrderItemDTO> items,ShipmentAddress address);

	public List<ShipmentResponse> getShipmentsByOrderId(UUID orderId);

	List<ShipmentTrackingResponse> getTrackingHistory(String trackingNumber);

	void updateShipmentStatus(UUID shipmentId, String status, String location);

	boolean isShipmentAlreadyCreated(UUID orderId);

}