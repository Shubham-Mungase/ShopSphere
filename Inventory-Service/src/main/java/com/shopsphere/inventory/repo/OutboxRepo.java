package com.shopsphere.inventory.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.inventory.entity.OutboxEntity;
import com.shopsphere.inventory.enums.OutboxStatus;

public interface OutboxRepo extends JpaRepository<OutboxEntity, UUID>{

	 List<OutboxEntity> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);

}
