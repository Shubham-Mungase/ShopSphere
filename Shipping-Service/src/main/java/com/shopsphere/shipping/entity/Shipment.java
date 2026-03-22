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
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID shipmentId;

    private UUID orderId;

    private UUID warehouseId;

    private String courierPartner;

    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Shipment() {
    }

    public Shipment(UUID shipmentId, UUID orderId, UUID warehouseId, String courierPartner, String trackingNumber,
    		ShipmentStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.shipmentId = shipmentId;
        this.orderId = orderId;
        this.warehouseId = warehouseId;
        this.courierPartner = courierPartner;
        this.trackingNumber = trackingNumber;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(UUID shipmentId) {
        this.shipmentId = shipmentId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(UUID warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getCourierPartner() {
        return courierPartner;
    }

    public void setCourierPartner(String courierPartner) {
        this.courierPartner = courierPartner;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}