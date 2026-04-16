package com.shopsphere.order.domain.dto;

import java.util.UUID;

public class CartItem {

	private UUID productId;
	private int quantity;
	private double price;

	public UUID getProductId() {
		return productId;
	}

	public void setProductId(UUID productId) {
		this.productId = productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public CartItem(UUID productId, int quantity, double price) {
		super();
		this.productId = productId;
		this.quantity = quantity;
		this.price = price;
	}

	public CartItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	
}
