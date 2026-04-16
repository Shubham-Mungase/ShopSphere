package com.shopsphere.user.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.user.dto.*;
import com.shopsphere.user.entity.*;
import com.shopsphere.user.exception.*;
import com.shopsphere.user.repo.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserProfileRepo userRepo;
    private final AddressRepo addressRepo;
    private final OutboxRepository outboxRepo;
    private final ObjectMapper mapper;

   

    

	public UserServiceImpl(UserProfileRepo userRepo, AddressRepo addressRepo, OutboxRepository outboxRepo,
			ObjectMapper mapper) {
		super();
		this.userRepo = userRepo;
		this.addressRepo = addressRepo;
		this.outboxRepo = outboxRepo;
		this.mapper = mapper;
	}

	private String getTraceId() {
        return MDC.get("X-Trace-Id");
    }

    // ================= USER =================

    @Override
    public UserProfileResponseDto createProfile(UUID userId, String email, UserProfileRequestDto req) {

        log.info("TraceId: {} | Creating profile for userId: {}", getTraceId(), userId);

        if (userRepo.existsByUserId(userId)) {
            log.warn("TraceId: {} | Profile already exists for userId: {}", getTraceId(), userId);
            throw new IllegalStateException("User profile already exists");
        }

        UserProfileEntity entity = new UserProfileEntity();
        entity.setUserId(userId);
        entity.setFullName(req.getFullName());
        entity.setPhno(req.getPhno());
        
        UserProfileEntity saved = userRepo.save(entity);
        
        UserCreatedEvent event=new UserCreatedEvent();
        event.setEmail(email);
        event.setName(saved.getFullName());
        event.setUserId(userId);
        
        try {
			String payload= mapper.writeValueAsString(event);
			OutboxEvent outbox=new OutboxEvent();
			
			
			 outbox.setAggregateType("USER");
			    outbox.setAggregateId(saved.getUser().toString());
			    outbox.setEventType("USER_CREATED");
			    outbox.setPayload(payload);
			    outboxRepo.save(outbox);
			
		} catch (JsonProcessingException e) {
			
			
			e.printStackTrace();
		}

        log.info("TraceId: {} | Profile created successfully for userId: {}", getTraceId(), userId);

        return mapToUserResponse(saved);
    }

    @Override
    public UserProfileResponseDto getProfile(UUID userId) {
        log.info("TraceId: {} | Fetching profile for userId: {}", getTraceId(), userId);
        return mapToUserResponse(getUserOrThrow(userId));
    }

    @Override
    public UserProfileResponseDto updateProfile(UUID userId, UserProfileRequestDto req) {

        log.info("TraceId: {} | Updating profile for userId: {}", getTraceId(), userId);

        UserProfileEntity user = getUserOrThrow(userId);

        user.setFullName(req.getFullName());
        user.setPhno(req.getPhno());

        UserProfileEntity updated = userRepo.save(user);

        log.info("TraceId: {} | Profile updated for userId: {}", getTraceId(), userId);

        return mapToUserResponse(updated);
    }

    @Override
    public boolean deleteUser(UUID userId) {

        log.warn("TraceId: {} | Deleting user with userId: {}", getTraceId(), userId);

        UserProfileEntity user = getUserOrThrow(userId);

        addressRepo.deleteAll(addressRepo.findAllByUserId(userId));
        userRepo.delete(user);

        log.warn("TraceId: {} | User deleted successfully with userId: {}", getTraceId(), userId);

        return true;
    }

    @Override
    public UserProfileSummaryDto getUserSummary(UUID userId) {

        log.info("TraceId: {} | Fetching user summary for userId: {}", getTraceId(), userId);

        UserProfileEntity entity = getUserOrThrow(userId);

        UserProfileSummaryDto dto = new UserProfileSummaryDto();
        BeanUtils.copyProperties(entity, dto);

        return dto;
    }

    @Override
    public List<UserProfileResponseDto> getAllUsers() {

        log.info("TraceId: {} | Fetching all users", getTraceId());

        return userRepo.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    // ================= ADDRESS =================

    @Override
    public AddressResponseDto addAddress(UUID userId, AddressRequestDto req) {

        log.info("TraceId: {} | Adding address for userId: {}", getTraceId(), userId);

        UserProfileEntity user = getUserOrThrow(userId);

        boolean isFirst = addressRepo.countByUserId(userId) == 0;

        AddressEntity entity = new AddressEntity();
        entity.setCity(req.getCity());
        entity.setState(req.getState());
        entity.setPincode(req.getPincode());
        entity.setUserId(userId);
        entity.setUserProfile(user);
        entity.setDefault(isFirst);

        AddressEntity saved = addressRepo.save(entity);

        log.info("TraceId: {} | Address added for userId: {}, addressId: {}", getTraceId(), userId, saved.getId());

        return mapToAddressResponse(saved);
    }

    @Override
    public List<AddressResponseDto> getAllAddress(UUID userId) {

        log.info("TraceId: {} | Fetching addresses for userId: {}", getTraceId(), userId);

        return addressRepo.findAllByUserId(userId)
                .stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponseDto updateAddress(UUID userId, UUID addressId, AddressRequestDto req) {

        log.info("TraceId: {} | Updating addressId: {} for userId: {}", getTraceId(), addressId, userId);

        AddressEntity entity = getAddressOrThrow(addressId);

        if (!entity.getUserId().equals(userId)) {
            log.warn("TraceId: {} | Unauthorized update attempt. userId: {}, addressId: {}", getTraceId(), userId, addressId);
            throw new UnauthorizedException("You are not allowed to update this address");
        }

        entity.setCity(req.getCity());
        entity.setState(req.getState());
        entity.setPincode(req.getPincode());

        AddressEntity updated = addressRepo.save(entity);

        log.info("TraceId: {} | Address updated: {}", getTraceId(), addressId);

        return mapToAddressResponse(updated);
    }

    @Override
    public boolean deleteAddress(UUID userId, UUID addressId) {

        log.warn("TraceId: {} | Deleting addressId: {} for userId: {}", getTraceId(), addressId, userId);

        AddressEntity address = getAddressOrThrow(addressId);

        if (!address.getUserId().equals(userId)) {
            log.warn("TraceId: {} | Unauthorized delete attempt. userId: {}, addressId: {}", getTraceId(), userId, addressId);
            throw new UnauthorizedException("You are not allowed to delete this address");
        }

        addressRepo.delete(address);

        log.warn("TraceId: {} | Address deleted: {}", getTraceId(), addressId);

        return true;
    }

    @Override
    public boolean setDefaultAddress(UUID userId, UUID addressId) {

        log.info("TraceId: {} | Setting default addressId: {} for userId: {}", getTraceId(), addressId, userId);

        List<AddressEntity> addresses = addressRepo.findAllByUserId(userId);

        if (addresses.isEmpty()) {
            log.error("TraceId: {} | No addresses found for userId: {}", getTraceId(), userId);
            throw new ResourceNotFoundException("No addresses found for user");
        }

        boolean found = false;

        for (AddressEntity addr : addresses) {
            if (addr.getId().equals(addressId)) {
                addr.setDefault(true);
                found = true;
            } else {
                addr.setDefault(false);
            }
        }

        if (!found) {
            log.error("TraceId: {} | Address not found for userId: {}, addressId: {}", getTraceId(), userId, addressId);
            throw new ResourceNotFoundException("Address not found for this user");
        }

        addressRepo.saveAll(addresses);

        log.info("TraceId: {} | Default address set: {}", getTraceId(), addressId);

        return true;
    }

    // ================= COMMON METHODS =================

    private UserProfileEntity getUserOrThrow(UUID userId) {
        UserProfileEntity user = userRepo.findByUserId(userId);

        if (user == null) {
            log.error("TraceId: {} | User not found with userId: {}", getTraceId(), userId);
            throw new UserNotFoundException("User not found");
        }

        return user;
    }

    private AddressEntity getAddressOrThrow(UUID addressId) {
        return addressRepo.findById(addressId)
                .orElseThrow(() -> {
                    log.error("TraceId: {} | Address not found with id: {}", getTraceId(), addressId);
                    return new ResourceNotFoundException("Address not found");
                });
    }

    private UserProfileResponseDto mapToUserResponse(UserProfileEntity entity) {
        UserProfileResponseDto dto = new UserProfileResponseDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private AddressResponseDto mapToAddressResponse(AddressEntity entity) {
        AddressResponseDto dto = new AddressResponseDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}