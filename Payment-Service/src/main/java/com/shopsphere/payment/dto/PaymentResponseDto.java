package com.shopsphere.payment.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.shopsphere.payment.enums.PaymentStatus;

public class PaymentResponseDto {
	
	private UUID paymentId;
	private UUID orderId;
	private UUID userId;
	private BigDecimal amount;

	private PaymentStatus status;
	
	
	

	public UUID getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(UUID paymentId) {
		this.paymentId = paymentId;
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}
	

}
