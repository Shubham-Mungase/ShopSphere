package com.shopsphere.payment.common.event;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentSuccessEvent {
    private UUID orderId;
    private UUID userId;
    private BigDecimal amount;
    private String paymentTransactionId;
	
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
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getPaymentTransactionId() {
		return paymentTransactionId;
	}
	public void setPaymentTransactionId(String paymentTransactionId) {
		this.paymentTransactionId = paymentTransactionId;
	}
	
	public PaymentSuccessEvent(UUID orderId, UUID userId, BigDecimal amount,
			String paymentTransactionId) {
		super();
		this.orderId = orderId;
		this.userId = userId;
		this.amount = amount;
		this.paymentTransactionId = paymentTransactionId;
	}
	public PaymentSuccessEvent() {
		super();
		// TODO Auto-generated constructor stub
	}
	

}
