package com.shopsphere.inventory.dto.event;


import java.util.UUID;

public class ProductCreatedEvent {
	private UUID productId;
	private String productName;
	private String eventType;
	
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	public UUID getProductId() {
		return productId;
	}
	public void setProductId(UUID productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public ProductCreatedEvent() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ProductCreatedEvent(UUID productId, String productName, String eventType) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.eventType = eventType;
	}
	
	

}
