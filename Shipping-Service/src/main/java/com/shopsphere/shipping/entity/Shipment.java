package com.shopsphere.shipping.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.shopsphere.shipping.enums.ShipmentStatus;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "shipments")
public class Shipment {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID shipmentId;

	private UUID orderId;

	private UUID warehouseId;

	private UUID productId;
	private Integer quantity;
	
	@Embedded
	@AttributeOverrides({
	    @AttributeOverride(name = "fullName", column = @Column(name = "address_full_name")),
	    @AttributeOverride(name = "phone", column = @Column(name = "address_phone")),
	    @AttributeOverride(name = "city", column = @Column(name = "address_city")),
	    @AttributeOverride(name = "pincode", column = @Column(name = "address_pincode")),
	    @AttributeOverride(name = "state", column = @Column(name = "address_state")),
	    @AttributeOverride(name = "isDefault", column = @Column(name = "address_is_default"))
	})
	private ShipmentAddress shipmentAddress;
	
	
	 // ✅ Items inside shipment
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL)
    private List<ShipmentItem> items;
    
    

	public List<ShipmentItem> getItems() {
		return items;
	}

	public void setItems(List<ShipmentItem> items) {
		this.items = items;
	}

	public Shipment(UUID shipmentId, UUID orderId, UUID warehouseId, UUID productId, Integer quantity,
			ShipmentAddress shipmentAddress, List<ShipmentItem> items, String courierPartner, String trackingNumber,
			ShipmentStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.shipmentId = shipmentId;
		this.orderId = orderId;
		this.warehouseId = warehouseId;
		this.productId = productId;
		this.quantity = quantity;
		this.shipmentAddress = shipmentAddress;
		this.items = items;
		this.courierPartner = courierPartner;
		this.trackingNumber = trackingNumber;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public ShipmentAddress getShipmentAddress() {
		return shipmentAddress;
	}

	public void setShipmentAddress(ShipmentAddress shipmentAddress) {
		this.shipmentAddress = shipmentAddress;
	}

	public UUID getProductId() {
		return productId;
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

	public Shipment(UUID shipmentId, UUID orderId, UUID warehouseId, UUID productId, Integer quantity,
			String courierPartner, String trackingNumber, ShipmentStatus status, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		super();
		this.shipmentId = shipmentId;
		this.orderId = orderId;
		this.warehouseId = warehouseId;
		this.productId = productId;
		this.quantity = quantity;
		this.courierPartner = courierPartner;
		this.trackingNumber = trackingNumber;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	private String courierPartner;

	private String trackingNumber;

	@Enumerated(EnumType.STRING)
	private ShipmentStatus status;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	public Shipment() {
	}

		public UUID getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(UUID shipmentId) {
		this.shipmentId = shipmentId;
	}

	public UUID getOrderId() {
		return orderId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

	public UUID getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(UUID warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getCourierPartner() {
		return courierPartner;
	}

	public void setCourierPartner(String courierPartner) {
		this.courierPartner = courierPartner;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public ShipmentStatus getStatus() {
		return status;
	}

	public void setStatus(ShipmentStatus status) {
		this.status = status;
	}

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
}