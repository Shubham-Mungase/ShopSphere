package com.shopsphere.inventory.dto.event;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentSuccessEvent {
	
	 private UUID orderId;
	    private UUID userId;
	    private BigDecimal amount;
	    private String paymentTransactionId;
	    
	    
	
		public UUID getUserId() {
			return userId;
		}
		public void setUserId(UUID userId) {
			this.userId = userId;
		}
		public String getPaymentTransactionId() {
			return paymentTransactionId;
		}
		public void setPaymentTransactionId(String paymentTransactionId) {
			this.paymentTransactionId = paymentTransactionId;
		}
		public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
		public UUID getOrderId() {
			return orderId;
		}
		public void setOrderId(UUID orderId) {
			this.orderId = orderId;
		}
		
		public PaymentSuccessEvent() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		
}
