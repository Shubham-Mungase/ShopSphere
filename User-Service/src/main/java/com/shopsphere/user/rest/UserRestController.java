package com.shopsphere.user.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.user.dto.AddressRequestDto;
import com.shopsphere.user.dto.AddressResponseDto;
import com.shopsphere.user.dto.UserProfileRequestDto;
import com.shopsphere.user.dto.UserProfileResponseDto;
import com.shopsphere.user.dto.UserProfileSummaryDto;
import com.shopsphere.user.service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    private String getUserId() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();
    }


    @PostMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> createProfile(
            @RequestBody @Valid UserProfileRequestDto req) {
    	
    	System.out.println(req.getPhno());

        return ResponseEntity.ok(
                userService.createProfile(getUserId(), req)
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> getProfile() {
        return ResponseEntity.ok(
                userService.getProfile(getUserId())
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
            @RequestBody @Valid UserProfileRequestDto req) {
        return ResponseEntity.ok(
                userService.updateProfile(getUserId(), req)
        );
    }

    @PostMapping("/address")
    public ResponseEntity<AddressResponseDto> addAddress(
            @RequestBody @Valid AddressRequestDto req
           ) {

        return ResponseEntity.ok(
                userService.addAddress(getUserId(), req)
        );
    }

    @GetMapping("/address")
    public ResponseEntity<List<AddressResponseDto>> getAllAddress(
            ) {

        return ResponseEntity.ok(
                userService.getAllAddress(getUserId())
        );
    }

    @PutMapping("/address/{addressId}")
    public ResponseEntity<AddressResponseDto> updateAddress(
            @PathVariable Integer addressId,
            @RequestBody @Valid AddressRequestDto req) {

        return ResponseEntity.ok(
                userService.updateAddress(getUserId(), addressId, req)
        );
    }

    @PutMapping("/address/{addressId}/default")
    public ResponseEntity<String> setDefault(
            @PathVariable Integer addressId
            ) {

        userService.setDefaultAddress(getUserId(), addressId);
        return ResponseEntity.ok("Default address set");
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<String> deleteAddress(
            @PathVariable Integer addressId
            ) {

        userService.deleteAddress(getUserId(), addressId);
        return ResponseEntity.ok("Address deleted");
    }

    @GetMapping("/summary")
    public ResponseEntity<UserProfileSummaryDto> summary() {
        return ResponseEntity.ok(
                userService.getUserSummary(getUserId())
        );
    }
}
