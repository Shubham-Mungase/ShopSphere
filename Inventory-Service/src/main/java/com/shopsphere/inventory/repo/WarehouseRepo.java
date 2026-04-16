package com.shopsphere.inventory.repo;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.inventory.entity.Warehouse;

public interface WarehouseRepo extends JpaRepository<Warehouse, UUID> {
}