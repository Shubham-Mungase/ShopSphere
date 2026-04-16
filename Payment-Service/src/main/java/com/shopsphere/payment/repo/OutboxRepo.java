package com.shopsphere.payment.repo;


import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopsphere.payment.entity.OutboxEntity;
import com.shopsphere.payment.enums.OutboxStatus;

@Repository
public interface OutboxRepo extends JpaRepository<OutboxEntity, UUID> {

    List<OutboxEntity> findByStatus(OutboxStatus status);

}