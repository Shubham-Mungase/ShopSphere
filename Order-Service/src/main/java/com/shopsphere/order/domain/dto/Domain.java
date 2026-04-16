package com.shopsphere.order.domain.dto;

import java.time.LocalDateTime;

public class Domain<T> {

	private String eventId;
	private String eventType;
	private LocalDateTime occurredOn;
	private T payload;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public LocalDateTime getOccurredOn() {
		return occurredOn;
	}

	public void setOccurredOn(LocalDateTime occurredOn) {
		this.occurredOn = occurredOn;
	}

	public Domain(String eventId, String eventType, LocalDateTime occurredOn, T payload) {
		super();
		this.eventId = eventId;
		this.eventType = eventType;
		this.occurredOn = occurredOn;
		this.payload = payload;
	}

	public T getPayload() {
		return payload;
	}

	public void setPayload(T payload) {
		this.payload = payload;
	}

	public Domain() {
		super();
		// TODO Auto-generated constructor stub
	}

}
