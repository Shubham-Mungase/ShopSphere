package com.shopsphere.inventory.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shopsphere.inventory.enums.OutboxStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "outbox-inventory")
public class OutboxEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "event_id", nullable = false, unique = true)
	private String eventId;

	@Column(name = "aggregate_type")
	private String aggregateType;

	@Column(name = "aggregate_id")
	private String aggregateId;

	@Column(name = "event_type", nullable = false)
	private String eventType;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String payload;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OutboxStatus status; // NEW, SENT, FAILED

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getAggregateType() {
		return aggregateType;
	}

	public void setAggregateType(String aggregateType) {
		this.aggregateType = aggregateType;
	}

	public String getAggregateId() {
		return aggregateId;
	}

	public void setAggregateId(String aggregateId) {
		this.aggregateId = aggregateId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public OutboxStatus getStatus() {
		return status;
	}

	public void setStatus(OutboxStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public OutboxEntity(String eventId, String aggregateType, String aggregateId, String eventType, String payload,
			OutboxStatus status) {
		super();
		this.eventId = eventId;
		this.aggregateType = aggregateType;
		this.aggregateId = aggregateId;
		this.eventType = eventType;
		this.payload = payload;
		this.status = status;
		this.createdAt = LocalDateTime.now();
	}

	public OutboxEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}
