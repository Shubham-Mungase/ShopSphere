package com.shopsphere.inventory.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ReserveStockRequest {

	private UUID orderId;
	private UUID userId;
	private BigDecimal totalAmount;
	List<ReserveStockItem> requests;
	public ReserveStockRequest(UUID orderId, UUID userId, BigDecimal totalAmount, List<ReserveStockItem> requests) {
		super();
		this.orderId = orderId;
		this.userId = userId;
		this.totalAmount = totalAmount;
		this.requests = requests;
	}
	public ReserveStockRequest() {
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
	public List<ReserveStockItem> getRequests() {
		return requests;
	}
	public void setRequests(List<ReserveStockItem> requests) {
		this.requests = requests;
	}

	
	

}
