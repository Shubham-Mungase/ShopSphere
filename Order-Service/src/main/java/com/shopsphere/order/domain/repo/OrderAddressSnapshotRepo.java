package com.shopsphere.order.domain.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.order.domain.entity.OrderAddressSnapshot;

public interface OrderAddressSnapshotRepo extends JpaRepository<OrderAddressSnapshot, UUID>{

}
