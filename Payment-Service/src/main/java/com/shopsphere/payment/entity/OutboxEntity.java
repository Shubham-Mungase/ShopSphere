package com.shopsphere.payment.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shopsphere.payment.enums.OutboxStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_outbox")
public class OutboxEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "event_id", nullable = false, unique = true, length = 100)
	private String eventId;

	@Column(name = "aggregate_type", nullable = false, length = 100)
	private String aggregateType;

	@Column(name = "aggregate_id", nullable = false, length = 100)
	private String aggregateId;

	@Column(name = "event_type", nullable = false, length = 100)
	private String eventType;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String payload;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OutboxStatus status;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	// 🔥 Constructor used in service
	public OutboxEntity(String eventId, String aggregateType, String aggregateId, String eventType, String payload,
			OutboxStatus status) {

		this.eventId = eventId;
		this.aggregateType = aggregateType;
		this.aggregateId = aggregateId;
		this.eventType = eventType;
		this.payload = payload;
		this.status = status;
		this.createdAt = LocalDateTime.now();
	}

	public OutboxEntity() {
	}

	// Getters & Setters

	public UUID getId() {
		return id;
	}

	public String getEventId() {
		return eventId;
	}

	public String getAggregateType() {
		return aggregateType;
	}

	public String getAggregateId() {
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public void setAggregateType(String aggregateType) {
		this.aggregateType = aggregateType;
	}

	public void setAggregateId(String aggregateId) {
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

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	

}