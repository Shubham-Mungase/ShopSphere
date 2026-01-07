package com.shopsphere.product.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.product.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

	Optional<Category> findByName(String name);
	
	 List<Category>  findByParentIsNull();

}
