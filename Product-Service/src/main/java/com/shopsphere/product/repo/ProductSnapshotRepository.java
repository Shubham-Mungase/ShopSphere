package com.shopsphere.product.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.product.entity.ProductSnapshotEntity;

public interface ProductSnapshotRepository extends JpaRepository<ProductSnapshotEntity, UUID>{

}
