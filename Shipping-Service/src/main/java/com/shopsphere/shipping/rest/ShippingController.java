package com.shopsphere.shipping.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.shipping.dto.response.ShipmentResponse;
import com.shopsphere.shipping.dto.response.ShipmentTrackingResponse;
import com.shopsphere.shipping.service.ShipmentService;

@RestController
@RequestMapping("/api/shipments")
public class ShippingController {

    private final ShipmentService shipmentService;

    public ShippingController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    
	/*
	 * @PostMapping("/test/create/{orderId}/{productId}") public
	 * ResponseEntity<String> createTestShipment(@PathVariable UUID
	 * orderId,@PathVariable UUID productId) {
	 * 
	 * shipmentService.createShipment(orderId,productId);
	 * 
	 * return ResponseEntity.ok("Shipment created"); }
	 */
    /**
     * Get shipment by orderId
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity< List<ShipmentResponse>> getShipmentByOrderId(
            @PathVariable UUID orderId) {

        List<ShipmentResponse> shipmentsByOrderId = shipmentService.getShipmentsByOrderId(orderId);

        return ResponseEntity.ok(shipmentsByOrderId);
    }

    /**
     * Track shipment using tracking number
     */
    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<List<ShipmentTrackingResponse>> trackShipment(
            @PathVariable String trackingNumber) {

        List<ShipmentTrackingResponse> tracking =
                shipmentService.getTrackingHistory(trackingNumber);

        return ResponseEntity.ok(tracking);
    }

    /**
     * Update shipment status (simulate courier updates)
     */
    @PatchMapping("/{shipmentId}/status")
    public ResponseEntity<String> updateShipmentStatus(
            @PathVariable UUID shipmentId,
            @RequestParam String status,
            @RequestParam String location) {

        shipmentService.updateShipmentStatus(shipmentId, status, location);

        return ResponseEntity.ok("Shipment status updated");
    }

}