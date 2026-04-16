package com.shopsphere.payment.common.event;

import java.time.LocalDateTime;

public class DomainEvent<T> {
	private String eventId;
    private String eventType;
    private LocalDateTime occurredOn;
    private T payload;
	public DomainEvent() {
		super();
		// TODO Auto-generated constructor stub
	}
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
	public T getPayload() {
		return payload;
	}
	public void setPayload(T payload) {
		this.payload = payload;
	}
	public DomainEvent(String eventId, String eventType, LocalDateTime occurredOn, T payload) {
		super();
		this.eventId = eventId;
		this.eventType = eventType;
		this.occurredOn = occurredOn;
		this.payload = payload;
	}

}
