package com.shopsphere.user.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.user.entity.UserSnapshotEntity;

public interface UserSnapshotRepo extends JpaRepository<UserSnapshotEntity, UUID> {

	Optional<UserSnapshotEntity>
	findByIdAndUserIdAndDefaultAddressTrue(UUID id, UUID userId);
}
