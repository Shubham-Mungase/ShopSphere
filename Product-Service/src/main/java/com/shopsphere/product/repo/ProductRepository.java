package com.shopsphere.product.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.shopsphere.product.entity.Product;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, UUID>,JpaSpecificationExecutor<Product> {

	List<Product> findByActiveTrue();

	List<Product> findByCategoryIdAndActiveTrue(UUID categoryId);

	List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String keyword);

}
