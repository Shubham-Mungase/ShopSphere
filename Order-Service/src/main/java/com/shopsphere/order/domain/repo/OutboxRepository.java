package com.shopsphere.order.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.order.domain.entity.OutboxEntity;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEntity, UUID> {

    List<OutboxEntity> findByStatus(String status);
}