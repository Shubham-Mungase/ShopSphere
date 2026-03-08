package com.shopsphere.inventory.dto.event;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class InventoryReservationFailedEvent {

	private UUID orderId;
    private UUID userId;
    private BigDecimal totalAmount;
    private List<UUID> productIds;
    private String status;
	public InventoryReservationFailedEvent() {
		super();
		// TODO Auto-generated constructor stub
	}
	public InventoryReservationFailedEvent(UUID orderId, UUID userId, BigDecimal totalAmount, List<UUID> productIds,
			String status) {
		super();
		this.orderId = orderId;
		this.userId = userId;
		this.totalAmount = totalAmount;
		this.productIds = productIds;
		this.status = status;
	}
	public UUID getOrderId() {
		return orderId;
	}
	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
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
    
	   
    
}
