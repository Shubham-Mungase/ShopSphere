package com.shopsphere.user.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shopsphere.user.dto.UserSnapshotResponseDto;
import com.shopsphere.user.entity.AddressEntity;
import com.shopsphere.user.entity.UserProfileEntity;
import com.shopsphere.user.entity.UserSnapshotEntity;
import com.shopsphere.user.repo.AddressRepo;
import com.shopsphere.user.repo.UserProfileRepo;
import com.shopsphere.user.repo.UserSnapshotRepo;

@Service
public class UserSnapshotServiceImpl implements UserSnapshotService{

	private final UserProfileRepo profileRepo;
	private final AddressRepo addressRepo;
	private final UserSnapshotRepo snapshotRepo;
	
	
	public UserSnapshotServiceImpl(UserProfileRepo profileRepo, AddressRepo addressRepo,
			UserSnapshotRepo snapshotRepo) {
		super();
		this.profileRepo = profileRepo;
		this.addressRepo = addressRepo;
		this.snapshotRepo = snapshotRepo;
	}


	@Override
	public UserSnapshotResponseDto createUserSnapshot(UUID userId, UUID addressId) {

	    UserProfileEntity profileEntity = profileRepo.findByUserId(userId);
	    if(profileEntity==null)
	    {
	    	throw new RuntimeException("User Not Found to create snapshot with this id ="+ userId);
	    }
	           
	                  
	    AddressEntity addressEntity = addressRepo.findById(addressId)
	            .orElseThrow(() -> new RuntimeException(
	                    "Address Not Found with id = " + addressId));

	    if (!addressEntity.getUserId().equals(userId)) {
	        throw new RuntimeException("Address does not belong to the user");
	    }

	    UserSnapshotEntity entity = new UserSnapshotEntity();

	    entity.setUserId(userId);
	    entity.setFullName(profileEntity.getFullName());
	    entity.setPhone(profileEntity.getPhno());

	    entity.setCity(addressEntity.getCity());
	    entity.setState(addressEntity.getState());
	    entity.setPincode(addressEntity.getPincode());
	    entity.setDefaultAddress(addressEntity.isDefault());

	    UserSnapshotEntity savedSnapshot = snapshotRepo.save(entity);

	    UserSnapshotResponseDto dto = new UserSnapshotResponseDto();
	    dto.setUserId(savedSnapshot.getUserId());
	    dto.setFullName(savedSnapshot.getFullName());
	    dto.setPhone(savedSnapshot.getPhone());
	    dto.setCity(savedSnapshot.getCity());
	    dto.setState(savedSnapshot.getState());
	    dto.setPincode(savedSnapshot.getPincode());
	    dto.setDefaultAddress(savedSnapshot.isDefaultAddress());

	    return dto;
	}

	
	
}
