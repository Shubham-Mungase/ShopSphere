package com.shopsphere.payment.common.event;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class InventoryReservedEvent {
	private UUID orderId;
	private UUID userId;
	private BigDecimal totalAmount;
	private List<UUID> productIds;
	private String status;

	public InventoryReservedEvent(UUID orderId, UUID userId, BigDecimal totalAmount, List<UUID> productIds,
			String status) {
		super();
		this.orderId = orderId;
		this.userId = userId;
		this.totalAmount = totalAmount;
		this.productIds = productIds;
		this.status = status;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public UUID getOrderId() {
		return orderId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

	public List<UUID> getProductIds() {
		return productIds;
	}

	public void setProductIds(List<UUID> productIds) {
		this.productIds = productIds;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public InventoryReservedEvent() {
		super();
		// TODO Auto-generated constructor stub
	}
}