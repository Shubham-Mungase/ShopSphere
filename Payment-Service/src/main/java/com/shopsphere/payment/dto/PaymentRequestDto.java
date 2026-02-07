package com.shopsphere.payment.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentRequestDto {
	
	private UUID orderId;
	private UUID userId;
	private BigDecimal amount;
	private String paymentMethod;

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

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	} 
	

}
