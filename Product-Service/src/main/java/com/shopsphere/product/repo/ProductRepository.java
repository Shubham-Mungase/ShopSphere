package com.shopsphere.product.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.shopsphere.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, UUID>,JpaSpecificationExecutor<Product> {

	List<Product> findByActiveTrue();

	List<Product> findByCategoryIdAndActiveTrue(UUID categoryId);

	List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String keyword);

}
