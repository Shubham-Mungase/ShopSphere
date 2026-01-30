package com.shopsphere.order.domain.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="order_product_snapshot")
public class OrderProductSnapshot {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false)
	private UUID userId;
	
	@Column(nullable = false)
	private UUID productId;
	
	@Column(nullable = false ,name="product_name")
	private String productName;
	
	@Column(nullable = false)
	private String imageUrl;
	
	@Column(nullable = false,name = "final_price")
	private BigDecimal finalPrice;
	
	private BigDecimal discount;
	
	@Column(nullable = false)
	private BigDecimal price;

	public OrderProductSnapshot() {
		super();
	}
	
	

	public OrderProductSnapshot(UUID id, UUID userId, String productName, String imageUrl, BigDecimal finalPrice,
			BigDecimal discount, BigDecimal price) {
		super();
		this.id = id;
		this.userId = userId;
		this.productName = productName;
		this.imageUrl = imageUrl;
		this.finalPrice = finalPrice;
		this.discount = discount;
		this.price = price;
	}

	
	


	public UUID getProductId() {
		return productId;
	}



	public void setProductId(UUID productId) {
		this.productId = productId;
	}



	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public BigDecimal getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(BigDecimal finalPrice) {
		this.finalPrice = finalPrice;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	

}
