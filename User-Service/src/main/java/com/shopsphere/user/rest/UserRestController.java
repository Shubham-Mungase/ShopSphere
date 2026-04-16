package com.shopsphere.user.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.user.dto.*;
import com.shopsphere.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class UserRestController {

	
	
	
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    // 🔥 Extract user info from SecurityContext
    private String getUsername(Authentication auth) {
        return auth.getName();
    }

    private UUID getUserId(Authentication auth) {
        // we will pass userId via header also (recommended)
        Object userId = auth.getDetails();
        return userId != null ? UUID.fromString(userId.toString()) : null;
    }

    @PostMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> createProfile(
            @RequestBody @Valid UserProfileRequestDto req,
            Authentication auth) {

        return ResponseEntity.status(201)
                .body(userService.createProfile(
                        getUserId(auth),
                        getUsername(auth),
                        req));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> getProfile(
            Authentication auth) {

        return ResponseEntity.ok(
                userService.getProfile(getUserId(auth)));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
            @RequestBody @Valid UserProfileRequestDto req,
            Authentication auth) {

        return ResponseEntity.ok(
                userService.updateProfile(getUserId(auth), req));
    }

    @PostMapping("/address")
    public ResponseEntity<AddressResponseDto> addAddress(
            @RequestBody @Valid AddressRequestDto req,
            Authentication auth) {

        return ResponseEntity.status(201)
                .body(userService.addAddress(getUserId(auth), req));
    }

    @GetMapping("/address")
    public ResponseEntity<List<AddressResponseDto>> getAllAddress(
            Authentication auth) {

        return ResponseEntity.ok(
                userService.getAllAddress(getUserId(auth)));
    }

    @PutMapping("/address/{addressId}")
    public ResponseEntity<AddressResponseDto> updateAddress(
            @PathVariable UUID addressId,
            @RequestBody @Valid AddressRequestDto req,
            Authentication auth) {

        return ResponseEntity.ok(
                userService.updateAddress(getUserId(auth), addressId, req));
    }

    @PutMapping("/address/{addressId}/default")
    public ResponseEntity<Void> setDefault(
            @PathVariable UUID addressId,
            Authentication auth) {

        userService.setDefaultAddress(getUserId(auth), addressId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable UUID addressId,
            Authentication auth) {

        userService.deleteAddress(getUserId(auth), addressId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<UserProfileSummaryDto> summary(
            Authentication auth) {

        return ResponseEntity.ok(
                userService.getUserSummary(getUserId(auth)));
    }
}