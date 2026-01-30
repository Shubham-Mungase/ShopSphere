package com.shopsphere.order.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderResponseDto {
	
	private UUID orderId;

	private String orderStatus;

	private BigDecimal totalAmount;

	private LocalDateTime createdAt;

	private List<OrderItemResponseDto> items;

	private ShippingAddressResponseDto shippingAddress;

	public UUID getOrderId() {
		return orderId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<OrderItemResponseDto> getItems() {
		return items;
	}

	public void setItems(List<OrderItemResponseDto> items) {
		this.items = items;
	}

	public ShippingAddressResponseDto getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(ShippingAddressResponseDto shippingAddress) {
		this.shippingAddress = shippingAddress;
	}
	
	

}
