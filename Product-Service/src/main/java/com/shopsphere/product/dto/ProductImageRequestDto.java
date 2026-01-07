package com.shopsphere.product.dto;

import jakarta.validation.constraints.NotNull;

public class ProductImageRequestDto {

	@NotNull
    private String imageUrl;
	
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
}
