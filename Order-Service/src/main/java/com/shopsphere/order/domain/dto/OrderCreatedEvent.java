package com.shopsphere.order.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderCreatedEvent {

	private UUID eventId;
	private UUID orderId;
	private UUID userId;
	private String aggregateType; // "ORDER"
	List<OrderItemEvent> items;
	private BigDecimal totalAmount;

	private String status;
	private LocalDateTime createdAt;

	public UUID getEventId() {
		return eventId;
	}

	public void setEventId(UUID eventId) {
		this.eventId = eventId;
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

	public List<OrderItemEvent> getItems() {
		return items;
	}

	public void setItems(List<OrderItemEvent> items) {
		this.items = items;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getAggregateType() {
		return aggregateType;
	}

	public void setAggregateType(String aggregateType) {
		this.aggregateType = aggregateType;
	}

	public OrderCreatedEvent(UUID eventId, UUID orderId, UUID userId, String aggregateType, List<OrderItemEvent> items,
			BigDecimal totalAmount, String status, LocalDateTime createdAt) {
		super();
		this.eventId = eventId;
		this.orderId = orderId;
		this.userId = userId;
		this.aggregateType = aggregateType;
		this.items = items;
		this.totalAmount = totalAmount;
		this.status = status;
		this.createdAt = createdAt;
	}
}
