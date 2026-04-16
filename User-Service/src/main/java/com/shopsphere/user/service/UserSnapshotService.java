package com.shopsphere.user.service;

import java.util.UUID;

import com.shopsphere.user.dto.UserSnapshotResponseDto;

public interface UserSnapshotService {

	public UserSnapshotResponseDto createUserSnapshot(UUID userId, UUID addressId);
	
}
