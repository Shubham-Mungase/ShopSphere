package com.shopsphere.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.user.dto.AddressRequestDto;
import com.shopsphere.user.dto.AddressResponseDto;
import com.shopsphere.user.dto.UserProfileRequestDto;
import com.shopsphere.user.dto.UserProfileResponseDto;
import com.shopsphere.user.dto.UserProfileSummaryDto;
import com.shopsphere.user.entity.AddressEntity;
import com.shopsphere.user.entity.UserProfileEntity;
import com.shopsphere.user.repo.AddressRepo;
import com.shopsphere.user.repo.UserProfileRepo;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private final UserProfileRepo userRepo;

	private final AddressRepo addressRepo;

	public UserServiceImpl(UserProfileRepo userRepo, AddressRepo addressRepo) {
		super();
		this.userRepo = userRepo;
		this.addressRepo = addressRepo;
	}

	@Override
	public UserProfileResponseDto createProfile(String userId, UserProfileRequestDto req) {

		if (userRepo.existsByUserId(userId)) {
			throw new RuntimeException("User profile already exists");
		}
		UserProfileEntity entity = new UserProfileEntity();
		entity.setUserId(userId);
		entity.setFullName(req.getFullName());
		entity.setPhno(req.getPhno());

		UserProfileEntity saved = userRepo.save(entity);

		UserProfileResponseDto res = new UserProfileResponseDto();
		BeanUtils.copyProperties(saved, res);

		return res;
	}

	@Override
	public UserProfileResponseDto getProfile(String userId) {
		UserProfileEntity userEntity = userRepo.findByUserId(userId);
		UserProfileResponseDto dto = new UserProfileResponseDto();
		BeanUtils.copyProperties(userEntity, dto);
		return dto;
	}

	@Override
	public UserProfileResponseDto updateProfile(String userId, UserProfileRequestDto req) {

		UserProfileEntity userEntity = userRepo.findByUserId(userId);
		UserProfileEntity save = null;
		if (userEntity != null) {
			userEntity.setFullName(req.getFullName());
			userEntity.setPhno(req.getPhno());

			save = userRepo.save(userEntity);

		}
		UserProfileResponseDto dto = new UserProfileResponseDto();

		BeanUtils.copyProperties(save, dto);

		return dto;
	}

	@Override
	public AddressResponseDto addAddress(String userId, AddressRequestDto req) {
		UserProfileEntity user = userRepo.findByUserId(userId);
		
		AddressEntity entity=new  AddressEntity();
		boolean firstAddress=addressRepo.countByUserId(userId)==0;
		if (user!=null) {
			entity.setCity(req.getCity());
			entity.setDefault(firstAddress);
			entity.setPincode(req.getPincode());
			entity.setState(req.getState());
			entity.setUserId(userId);
			entity.setUserProfile(user);
			
		}
		
		AddressEntity save = addressRepo.save(entity);
		AddressResponseDto dto=new AddressResponseDto();
		BeanUtils.copyProperties(save, dto);
		
		return dto;
	}

	@Override
	public List<AddressResponseDto> getAllAddress(String userId) {
		
		List<AddressEntity> allByUserId = addressRepo.findAllByUserId(userId);
		
		List<AddressResponseDto> list=new ArrayList<>();
		for (AddressEntity entity : allByUserId) {
			AddressResponseDto dto=new AddressResponseDto();
			BeanUtils.copyProperties(entity, dto);
			list.add(dto);
		}
		return list;
	}

	@Override
	public AddressResponseDto updateAddress(String userId, Integer addressId, AddressRequestDto req) {
		
		AddressEntity entity = addressRepo.findById(addressId).orElseThrow(()->new RuntimeException("Address not found"));
		
		if(!entity.getUserId().equals(userId))
		{
			throw new RuntimeException("Unauthorized");
		}
		AddressResponseDto dto=new AddressResponseDto();
		entity.setCity(req.getCity());
		entity.setPincode(req.getPincode());
		entity.setState(req.getState());
		
		AddressEntity addressEntity = addressRepo.save(entity);
		BeanUtils.copyProperties(addressEntity, dto);
		return dto;
	}

	@Override
	public boolean setDefaultAddress(String userId, Integer addressId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteAddress(String userId, Integer addressId) {
		AddressEntity address = addressRepo.findById(addressId).orElseThrow();
		
		boolean status=false;
		
		if(!address.getUserId().equals(userId))
		{
			throw new RuntimeException("Unauthorized");
		}
		if(address!=null)
		{
			addressRepo.delete(address);
			status=true;
		}
		
		return status;
	}

	@Override
	public UserProfileSummaryDto getUserSummary(String userId) {
		
		UserProfileEntity entity = userRepo.findByUserId(userId);
		UserProfileSummaryDto dto=new UserProfileSummaryDto();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

}
