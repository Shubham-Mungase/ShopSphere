package com.shopsphere.product.service;

import java.util.UUID;

import com.shopsphere.product.dto.ProductSnapshotDto;

public interface ProductSnapShotService {

	public ProductSnapshotDto createSnapshot(UUID productId);
	
}
