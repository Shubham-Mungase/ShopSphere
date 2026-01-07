package com.shopsphere.product.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.product.entity.ProductImage;
import java.util.List;


public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

	List<ProductImage> findByProductId(UUID productId);
}
