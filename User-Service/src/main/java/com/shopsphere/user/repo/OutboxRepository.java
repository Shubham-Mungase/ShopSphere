package com.shopsphere.user.repo;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.user.entity.OutboxEvent;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, String> {

    List<OutboxEvent> findByStatus(String status);
}