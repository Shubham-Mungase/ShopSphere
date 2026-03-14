package com.shopsphere.cart.document;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.shopsphere.cart.enums.OutboxStatus;

@Document(collection = "outbox_events")
public class OutboxEvent {

	@Id
	private String id;

	private String aggregateType;
	private UUID aggregateId;
	private String eventType;
	private String payload;
	private OutboxStatus status;
	private Instant createdAt;

	public OutboxEvent() {
	}

	public String getId() {
		return id;
	}

	public String getAggregateType() {
		return aggregateType;
	}

	public UUID getAggregateId() {
		return aggregateId;
	}

	public String getEventType() {
		return eventType;
	}

	public String getPayload() {
		return payload;
	}

	public OutboxStatus getStatus() {
		return status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setAggregateType(String aggregateType) {
		this.aggregateType = aggregateType;
	}

	public void setAggregateId(UUID aggregateId) {
		this.aggregateId = aggregateId;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public void setStatus(OutboxStatus status) {
		this.status = status;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
}