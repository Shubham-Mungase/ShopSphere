package com.shopsphere.order.domain.messages;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderCreatedEvent {
	
	 private UUID orderId;
	    private UUID userId;
	    private BigDecimal totalAmount;
	    private String status;
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
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public OrderCreatedEvent(UUID orderId, UUID userId, BigDecimal totalAmount, String status) {
			super();
			this.orderId = orderId;
			this.userId = userId;
			this.totalAmount = totalAmount;
			this.status = status;
		}
	  

}
