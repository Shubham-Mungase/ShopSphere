package com.shopsphere.inventory.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shopsphere.inventory.enums.MovementType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock_movement")
public class StockMovement {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID productId;

    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    @Column(nullable = false)
    private Integer quantity;

    private String reason;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getProductId() {
		return productId;
	}

	public void setProductId(UUID productId) {
		this.productId = productId;
	}

	public UUID getOrderId() {
		return orderId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

	public MovementType getType() {
		return type;
	}

	public void setType(MovementType type) {
		this.type = type;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}
