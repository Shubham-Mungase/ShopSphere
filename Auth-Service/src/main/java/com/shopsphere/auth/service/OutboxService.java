package com.shopsphere.auth.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.auth.entity.OutboxEvent;
import com.shopsphere.auth.repo.OutboxRepository;

@Service
public class OutboxService {

    private final OutboxRepository repo;
    private final ObjectMapper mapper;

    public OutboxService(OutboxRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public void saveEvent(String aggregateType,
                          String aggregateId,
                          String type,
                          Object payloadObj) {

        try {
            String payload = mapper.writeValueAsString(payloadObj);

            OutboxEvent event = new OutboxEvent();
            event.setId(UUID.randomUUID());
            event.setAggregateType(aggregateType);
            event.setAggregateId(aggregateId);
            event.setType(type);
            event.setPayload(payload);
            event.setStatus("PENDING");
            event.setCreatedAt(System.currentTimeMillis());

            repo.save(event);

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}