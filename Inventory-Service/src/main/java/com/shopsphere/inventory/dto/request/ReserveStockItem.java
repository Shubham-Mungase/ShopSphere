package com.shopsphere.inventory.dto.request;

import java.util.UUID;

public class ReserveStockItem {
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

	public ReserveStockItem(UUID productId, Integer quantity) {
		super();
		this.productId = productId;
		this.quantity = quantity;
	}

	public ReserveStockItem() {
		super();
		// TODO Auto-generated constructor stub
	}

}
