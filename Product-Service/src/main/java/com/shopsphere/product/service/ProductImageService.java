package com.shopsphere.product.service;

import java.util.List;
import java.util.UUID;

import com.shopsphere.product.dto.ProductImageRequestDto;
import com.shopsphere.product.dto.ProductImageResponseDto;

public interface ProductImageService {
	
	public ProductImageResponseDto addProductImage(UUID productId, ProductImageRequestDto req);
	
	public List<ProductImageResponseDto> getImagesByProduct(UUID productId);
	
	public boolean deleteImage(UUID imageId);
}
