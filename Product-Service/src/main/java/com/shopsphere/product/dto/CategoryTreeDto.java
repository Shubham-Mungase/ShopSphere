package com.shopsphere.product.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryTreeDto {
	
	private UUID id;
	private String name;
	private List<CategoryTreeDto> children=new ArrayList<>();
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
	public List<CategoryTreeDto> getChildren() {
		return children;
	}
	public void setChildren(List<CategoryTreeDto> children) {
		this.children = children;
	}
	
	

}
