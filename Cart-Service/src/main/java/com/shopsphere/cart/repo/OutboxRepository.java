package com.shopsphere.cart.repo;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.shopsphere.cart.document.OutboxEvent;
import com.shopsphere.cart.enums.OutboxStatus;

import java.util.List;

public interface OutboxRepository extends MongoRepository<OutboxEvent, String> {

    List<OutboxEvent> findByStatus(OutboxStatus status);
}