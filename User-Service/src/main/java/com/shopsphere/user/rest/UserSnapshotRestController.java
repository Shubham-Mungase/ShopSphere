package com.shopsphere.user.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.user.dto.UserSnapshotResponseDto;
import com.shopsphere.user.service.UserSnapshotService;

@RestController
@RequestMapping("/api/users/internal/users")
public class UserSnapshotRestController {

	private final UserSnapshotService service;

	public UserSnapshotRestController(UserSnapshotService service) {
		super();
		this.service = service;
	}
	
	
    private UUID getUserId(Authentication auth) {
        // we will pass userId via header also (recommended)
        Object userId = auth.getDetails();
        return userId != null ? UUID.fromString(userId.toString()) : null;
    }

	@GetMapping("/addresses/{addressId}")
	public ResponseEntity<?> getUserSnapshot(@PathVariable UUID addressId,Authentication auth) {		
		UserSnapshotResponseDto userSnapshot = service.createUserSnapshot(getUserId(auth),addressId);
		return ResponseEntity.ok(userSnapshot);
	}
}
