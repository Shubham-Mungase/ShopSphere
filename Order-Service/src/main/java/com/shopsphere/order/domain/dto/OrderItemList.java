package com.shopsphere.order.domain.dto;

import java.util.List;
import java.util.UUID;

public class OrderItemList {
	private UUID userId;
    private UUID addressId;
    private List<CartItem> items;
	public UUID getUserId() {
		return userId;
	}
	public void setUserId(UUID userId) {
		this.userId = userId;
	}
	public UUID getAddressId() {
		return addressId;
	}
	public void setAddressId(UUID addressId) {
		this.addressId = addressId;
	}
	public List<CartItem> getItems() {
		return items;
	}
	public void setItems(List<CartItem> items) {
		this.items = items;
	}
	public OrderItemList(UUID userId, UUID addressId, List<CartItem> items) {
		super();
		this.userId = userId;
		this.addressId = addressId;
		this.items = items;
	}
	public OrderItemList() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}
