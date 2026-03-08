package com.shopsphere.inventory.entity;

import java.util.UUID;

import com.shopsphere.inventory.enums.InventoryStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "inventory")
public class Inventory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "product_id", nullable = false, unique = true)
	private UUID productId;
	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Column(nullable = false)
	private String productName;
	
	
	@Column(nullable = false)
	private Integer availableStock;

	@Column(nullable = false)
	private Integer reservedStock;

	@Column(nullable = false)
	private Integer lowStockThreshold;

	@Enumerated(EnumType.STRING)
	private InventoryStatus status;

	@Version
	private Integer version;

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

	public Integer getAvailableStock() {
		return availableStock;
	}

	public void setAvailableStock(Integer availableStock) {
		this.availableStock = availableStock;
	}

	public Integer getReservedStock() {
		return reservedStock;
	}

	public void setReservedStock(Integer reservedStock) {
		this.reservedStock = reservedStock;
	}

	public Integer getLowStockThreshold() {
		return lowStockThreshold;
	}

	public void setLowStockThreshold(Integer lowStockThreshold) {
		this.lowStockThreshold = lowStockThreshold;
	}

	public InventoryStatus getStatus() {
		return status;
	}

	public void setStatus(InventoryStatus status) {
		this.status = status;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public void updateStatus() {
		if (availableStock == 0)
			status = InventoryStatus.OUT_OF_STOCK;
		else if (availableStock <= lowStockThreshold)
			status = InventoryStatus.LOW_STOCK;
		else
			status = InventoryStatus.IN_STOCK;
	}

}
