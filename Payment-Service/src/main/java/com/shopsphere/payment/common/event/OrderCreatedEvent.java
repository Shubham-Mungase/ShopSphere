package com.shopsphere.payment.common.event;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderEvents {

	private UUID orderId;
    private UUID userId;
    private BigDecimal totalAmount;
    private String status;
    
	public OrderEvents(UUID orderId, UUID userId, BigDecimal totalAmount, String status) {
		super();
		this.orderId = orderId;
		this.userId = userId;
		this.totalAmount = totalAmount;
		this.status = status;
	}
	public OrderEvents() {
		super();
		// TODO Auto-generated constructor stub
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
   
}
