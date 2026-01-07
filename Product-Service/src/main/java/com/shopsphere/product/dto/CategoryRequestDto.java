package com.shopsphere.product.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class CategoryRequestDto {

	@NotNull
	private String name;
	
	private UUID parentId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getParentId() {
		return parentId;
	}

	public void setParentId(UUID parentId) {
		this.parentId = parentId;
	}

	
	

}
