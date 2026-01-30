package com.shopsphere.order.domain.dto;

import java.util.List;
import java.util.UUID;

public class CreateOrderRequestDto {
	
	private UUID addressId;
	
	private List<OrderItemRequestDto> items;
	
	public UUID getAddressId() {
		return addressId;
	}

	public void setAddressId(UUID addressId) {
		this.addressId = addressId;
	}

	public List<OrderItemRequestDto> getItems() {
		return items;
	}

	public void setItems(List<OrderItemRequestDto> items) {
		this.items = items;
	}
	
	

}
