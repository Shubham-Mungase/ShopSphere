package com.shopsphere.product.service;

import java.util.List;
import java.util.UUID;

import com.shopsphere.product.dto.CategoryRequestDto;
import com.shopsphere.product.dto.CategoryResponseDto;
import com.shopsphere.product.dto.CategoryTreeDto;

public interface CategoryService {
	
	public CategoryResponseDto createCategory(CategoryRequestDto req);
	
	public List<CategoryResponseDto> getAllCategories();
	
	public CategoryResponseDto getCategory(UUID categoryId);
	
	public List<CategoryTreeDto> getCategoryChildren();
}
