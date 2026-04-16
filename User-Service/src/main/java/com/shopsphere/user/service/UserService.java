package com.shopsphere.user.service;

import java.util.List;
import java.util.UUID;

import com.shopsphere.user.dto.AddressRequestDto;
import com.shopsphere.user.dto.AddressResponseDto;
import com.shopsphere.user.dto.UserProfileRequestDto;
import com.shopsphere.user.dto.UserProfileResponseDto;
import com.shopsphere.user.dto.UserProfileSummaryDto;

public interface UserService {

	UserProfileResponseDto createProfile(UUID userId,String email, UserProfileRequestDto req);

	UserProfileResponseDto getProfile(UUID userId);

	UserProfileResponseDto updateProfile(UUID userId, UserProfileRequestDto req);

	AddressResponseDto addAddress(UUID userId, AddressRequestDto req);

	List<AddressResponseDto> getAllAddress(UUID userId);
	
	List<UserProfileResponseDto> getAllUsers();

	AddressResponseDto updateAddress(UUID userId, UUID addressId, AddressRequestDto req);

	boolean setDefaultAddress(UUID userId, UUID addressId);

	boolean deleteAddress(UUID userId, UUID addressId);

	boolean deleteUser(UUID userId);
	
	UserProfileSummaryDto getUserSummary(UUID userId);

}
