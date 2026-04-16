package com.shopsphere.shipping.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class ShipmentResponse {

	private UUID shipmentId;

	private UUID orderId;

	private UUID warehouseId;

	private String courierPartner;

	private String trackingNumber;
	private UUID productId;
	private Integer quantity;

	private String status;

	public UUID getProductId() {
		return productId;
	}

	public void setProductId(UUID productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
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