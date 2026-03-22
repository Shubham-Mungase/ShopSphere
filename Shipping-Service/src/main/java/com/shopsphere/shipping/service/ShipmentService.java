package com.shopsphere.shipping.service;

import java.util.List;
import java.util.UUID;

import com.shopsphere.shipping.dto.event.ShipmentEventDTO;
import com.shopsphere.shipping.dto.response.ShipmentResponse;
import com.shopsphere.shipping.dto.response.ShipmentTrackingResponse;

public interface ShipmentService {

    ShipmentEventDTO createShipment(UUID orderId,UUID productId);

    ShipmentResponse getShipmentByOrderId(UUID orderId);

    List<ShipmentTrackingResponse> getTrackingHistory(String trackingNumber);

    void updateShipmentStatus(UUID shipmentId, String status, String location);

}