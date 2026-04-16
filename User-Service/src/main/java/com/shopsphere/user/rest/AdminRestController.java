package com.shopsphere.user.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.user.dto.AddressResponseDto;
import com.shopsphere.user.dto.UserProfileResponseDto;
import com.shopsphere.user.service.UserService;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRestController {

    private final UserService userService;

    public AdminRestController(UserService userService) {
        this.userService = userService;
    }

    // 🔥 Get current admin username (for logging / audit)
    private String getAdmin(Authentication auth) {
        return auth.getName();
    }

    private UUID getAdminId(Authentication auth) {
        Object userId = auth.getDetails();
        return userId != null ? UUID.fromString(userId.toString()) : null;
    }

    @GetMapping
    public ResponseEntity<List<UserProfileResponseDto>> getAllUsers(Authentication auth) {

        System.out.println("Admin " + getAdmin(auth) + " fetched all users");

        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDto> getUser(
            @PathVariable UUID userId,
            Authentication auth) {

        System.out.println("Admin " + getAdmin(auth) + " fetched user " + userId);

        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID userId,
            Authentication auth) {

        UUID adminId = getAdminId(auth);

        // 🔥 Prevent self-delete (VERY IMPORTANT)
        if (adminId != null && adminId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }

        userService.deleteUser(userId);

        System.out.println("Admin " + getAdmin(auth) + " deleted user " + userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<AddressResponseDto>> getUserAddresses(
            @PathVariable UUID userId,
            Authentication auth) {

        System.out.println("Admin " + getAdmin(auth) + " fetched addresses for " + userId);

        return ResponseEntity.ok(userService.getAllAddress(userId));
    }
}