package com.shopsphere.order.domain.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="order_item")
public class OrderItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name = "order_id",nullable = false)
	private OrderEntity order;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name="product_snapshot_id",nullable = false)
	private OrderProductSnapshot productSnapshot;
	
	@Column(nullable = false)
	private Integer qunatity;
	
	@Column(nullable = false,name="price_at_purchase")
	private BigDecimal priceAtPurchase;
	
	@Column(nullable = false,name="total_price")
	private BigDecimal totalPrice;
	
	
	public OrderItem(OrderProductSnapshot productSnapshot,Integer quantity)
	{
		this.productSnapshot=productSnapshot;
		this.qunatity=quantity;
		this.priceAtPurchase=productSnapshot.getFinalPrice();
		this.totalPrice=this.priceAtPurchase.multiply(BigDecimal.valueOf(quantity));
		
		
	}
	
	public OrderItem()
	{	}

	public void setOrder(OrderEntity order) {
		this.order = order;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public OrderProductSnapshot getProductSnapshot() {
		return productSnapshot;
	}

	public void setProductSnapshot(OrderProductSnapshot productSnapshot) {
		this.productSnapshot = productSnapshot;
	}

	public Integer getQunatity() {
		return qunatity;
	}

	public void setQunatity(Integer qunatity) {
		this.qunatity = qunatity;
	}

	public BigDecimal getPriceAtPurchase() {
		return priceAtPurchase;
	}

	public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
		this.priceAtPurchase = priceAtPurchase;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public OrderEntity getOrder() {
		return order;
	}
	
	
	

	
}
