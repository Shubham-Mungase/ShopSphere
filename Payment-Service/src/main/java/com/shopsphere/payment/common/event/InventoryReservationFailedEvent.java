package com.shopsphere.payment.common.event;

import java.util.List;
import java.util.UUID;

public class InventoryReservationFailedEvent {

    private UUID orderId;
    private List<UUID> failedProductIds;
    private String reason;
	public UUID getOrderId() {
		return orderId;
	}
	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}
	
	public InventoryReservationFailedEvent(UUID orderId, List<UUID> failedProductIds, String reason) {
		super();
		this.orderId = orderId;
		this.failedProductIds = failedProductIds;
		this.reason = reason;
	}
	public List<UUID> getFailedProductIds() {
		return failedProductIds;
	}
	public void setFailedProductIds(List<UUID> failedProductIds) {
		this.failedProductIds = failedProductIds;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public InventoryReservationFailedEvent() {
		super();
	}
    
    
}
