package com.shopsphere.inventory.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public abstract class BaseEntity {

	@Column(name="created_at" ,updatable = false)
	private LocalDateTime createdAt;
	@Column(name="updated_at" ,updatable = true)
	private LocalDateTime updatedAt;
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	@PrePersist
	protected void onCreate() {
		this.createdAt=LocalDateTime.now()
				;
		this.updatedAt=LocalDateTime.now();
	}
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt=LocalDateTime.now();
	}
	
	
}
