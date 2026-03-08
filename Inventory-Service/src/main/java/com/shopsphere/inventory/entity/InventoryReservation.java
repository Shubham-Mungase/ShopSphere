package com.shopsphere.inventory.entity;

import java.util.UUID;

import com.shopsphere.inventory.enums.ReservationStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name="inventory_reservation")
public class InventoryReservation extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private UUID orderId;

	private UUID productId;

	private Integer quantity;

	@Enumerated(EnumType.STRING)
	private ReservationStatus status;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getOrderId() {
		return orderId;
	}


	public UUID getProductId() {
		return productId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

	public void setProductId(UUID productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public ReservationStatus getStatus() {
		return status;
	}

	public void setStatus(ReservationStatus status) {
		this.status = status;
	}

	


}
