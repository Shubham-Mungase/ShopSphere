package com.shopsphere.cart.document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "cart")
public class CartDocument {

	@Id
	private ObjectId cartId;

	private UUID userId;

	@Field("items")
	private List<CartItem> items;

	private Double totalPrice;

	private Integer totalItem;

	private Instant createdAt;

	private Instant updatedAt;

	public ObjectId getCartId() {
		return cartId;
	}

	public void setCartId(ObjectId cartId) {
		this.cartId = cartId;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public List<CartItem> getItems() {
		return items;
	}

	public void setItems(List<CartItem> items) {
		this.items = items;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public CartDocument(ObjectId cartId, UUID userId, List<CartItem> items, Double totalPrice, Integer totalItem,
			Instant createdAt, Instant updatedAt) {
		super();
		this.cartId = cartId;
		this.userId = userId;
		this.items = items;
		this.totalPrice = totalPrice;
		this.totalItem = totalItem;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public CartDocument() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getTotalItems() {
		return totalItem;
	}

	public void setTotalItems(Integer totalItem) {
		this.totalItem = totalItem;
	}

}
