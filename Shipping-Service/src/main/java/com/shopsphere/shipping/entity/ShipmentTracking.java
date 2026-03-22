package com.shopsphere.shipping.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shopsphere.shipping.enums.ShipmentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "shipment_tracking")
public class ShipmentTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID trackingId;

    private UUID shipmentId;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    private String location;

    private LocalDateTime timestamp;

    public ShipmentTracking() {
    }

    public ShipmentTracking(UUID trackingId, UUID shipmentId, ShipmentStatus status, String location, LocalDateTime timestamp) {
        this.trackingId = trackingId;
        this.shipmentId = shipmentId;
        this.status = status;
        this.location = location;
        this.timestamp = timestamp;
    }

    public UUID getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(UUID trackingId) {
        this.trackingId = trackingId;
    }

    public UUID getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(UUID shipmentId) {
        this.shipmentId = shipmentId;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}