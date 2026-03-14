package com.shopsphere.cart.dto.client;

import java.math.BigDecimal;
import java.util.UUID;


public class ProductResponse {
	
	private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean active;
    private UUID category;
	
	private String categoryName;
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public UUID getCategory() {
		return category;
	}
	public void setCategory(UUID category) {
		this.category = category;
	}

}
