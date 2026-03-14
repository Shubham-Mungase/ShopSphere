package com.shopsphere.cart.dto.request;

import java.util.UUID;

public class AddCartItemRequest {

	private UUID productId;

	private Integer quantity;

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

	public AddCartItemRequest(UUID productId, Integer quantity) {
		super();
		this.productId = productId;
		this.quantity = quantity;
	}

	public AddCartItemRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

}