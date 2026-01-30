package com.shopsphere.order.domain.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.order.domain.entity.OrderItem;

public interface OrderItemRepo extends JpaRepository<OrderItem, UUID> {

}
