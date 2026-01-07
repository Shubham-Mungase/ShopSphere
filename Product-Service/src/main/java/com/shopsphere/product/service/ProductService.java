package com.shopsphere.product.service;

import java.util.List;
import java.util.UUID;

import com.shopsphere.product.dto.PageResponse;
import com.shopsphere.product.dto.ProductRequestDto;
import com.shopsphere.product.dto.ProductResponseDto;
import com.shopsphere.product.dto.ProductSearchRequest;
import com.shopsphere.product.dto.ProductSearchResponse;

public interface ProductService {
	
	public ProductResponseDto createProduct(ProductRequestDto req);
	
	public ProductResponseDto getProduct(UUID productId);
	
	public List<ProductResponseDto> getAllProductsByCategory(UUID categoryId);
	
	public ProductResponseDto updateProduct(UUID productId,ProductRequestDto req);
	
	public boolean deactivateProduct(UUID productId);
	
	public PageResponse<ProductSearchResponse> searchProduct(ProductSearchRequest request);
	
	
	
}
