package com.shopsphere.product.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class Product extends BaseEntity{
	
	@Column(nullable = false)
	private String name;
	
	@Column(length = 2000)
	private String description;
	
	@Column(nullable = false)
	private BigDecimal price;
	
	private boolean active=true;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="category_id",nullable = false)
	private Category category; 
	
	
	@OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true)
	private List<ProductImage> images=new ArrayList<>();


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


	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {
		this.active = active;
	}


	public Category getCategory() {
		return category;
	}


	public void setCategory(Category category) {
		this.category = category;
	}


	public List<ProductImage> getImages() {
		return images;
	}


	public void setImages(List<ProductImage> images) {
		this.images = images;
	}
	
	

}
