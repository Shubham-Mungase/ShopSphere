package com.shopsphere.user.service;

import java.util.List;

import com.shopsphere.user.dto.AddressRequestDto;
import com.shopsphere.user.dto.AddressResponseDto;
import com.shopsphere.user.dto.UserProfileRequestDto;
import com.shopsphere.user.dto.UserProfileResponseDto;
import com.shopsphere.user.dto.UserProfileSummaryDto;

public interface UserService {

	UserProfileResponseDto createProfile(String userId, UserProfileRequestDto req);

	UserProfileResponseDto getProfile(String userId);

	UserProfileResponseDto updateProfile(String userId, UserProfileRequestDto req);

	AddressResponseDto addAddress(String userId, AddressRequestDto req);

	List<AddressResponseDto> getAllAddress(String userId);

	AddressResponseDto updateAddress(String userId, Integer addressId, AddressRequestDto req);

	boolean setDefaultAddress(String userId, Integer addressId);

	boolean deleteAddress(String userId, Integer addressId);

	UserProfileSummaryDto getUserSummary(String userId);

}
