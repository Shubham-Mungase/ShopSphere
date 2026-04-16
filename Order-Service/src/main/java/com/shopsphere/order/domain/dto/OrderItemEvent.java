package com.shopsphere.order.domain.dto;

import java.util.UUID;

public class OrderItemEvent {

	private UUID productId;
	private Integer quantity;

	public OrderItemEvent(UUID productId, Integer quantity) {
		super();
		this.productId = productId;
		this.quantity = quantity;
	}

	public UUID getProductId() {
		return productId;
	}

	public void setProductId(UUID productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
