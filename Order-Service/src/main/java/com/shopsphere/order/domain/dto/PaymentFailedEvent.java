package com.shopsphere.order.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentFailedEvent {
	private UUID orderId;
	private UUID paymentId;
	private BigDecimal amount;

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

	public UUID getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(UUID paymentId) {
		this.paymentId = paymentId;
	}

	public PaymentFailedEvent(UUID orderId, UUID paymentId, BigDecimal amount) {
		super();
		this.orderId = orderId;
		this.paymentId = paymentId;
		this.amount = amount;
	}

	public PaymentFailedEvent() {
		super();
		// TODO Auto-generated constructor stub
	}

}
