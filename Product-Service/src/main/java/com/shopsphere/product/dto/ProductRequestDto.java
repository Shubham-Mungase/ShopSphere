package com.shopsphere.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class ProductRequestDto {

	@NotNull
	private String name;
	@NotNull
	private String description;
	@NotNull
	private BigDecimal price;
	@NotNull
	private UUID categoryId;
	
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
	public UUID getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(UUID categoryId) {
		this.categoryId = categoryId;
	}
	
	
	

}
