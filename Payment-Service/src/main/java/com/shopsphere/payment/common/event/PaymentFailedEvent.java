package com.shopsphere.payment.common.event;

import java.util.UUID;

public class PaymentFailedEvent {

	    private UUID orderId;
	    private UUID userId;
	    private String reason;
		
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
		public String getReason() {
			return reason;
		}
		public void setReason(String reason) {
			this.reason = reason;
		}
		
		public PaymentFailedEvent( UUID orderId, UUID userId, String reason) {
			super();
			this.orderId = orderId;
			this.userId = userId;
			this.reason = reason;
		}
		public PaymentFailedEvent() {
			super();
			// TODO Auto-generated constructor stub
		}
	    
	    
	    
}
