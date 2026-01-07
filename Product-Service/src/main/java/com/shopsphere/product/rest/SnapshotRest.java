package com.shopsphere.product.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.product.dto.ProductSnapshotDto;
import com.shopsphere.product.service.ProductSnapShotService;

@RestController
@RequestMapping("/product")
public class SnapshotRest {

	private final ProductSnapShotService service;

	public SnapshotRest(ProductSnapShotService service) {
		super();
		this.service = service;
	}
	
	@PostMapping("/snapshot/{productId}")
	public ResponseEntity<ProductSnapshotDto> createSnapshot(
	        @PathVariable UUID productId) {

	  ProductSnapshotDto snapshot = service.createSnapshot(productId);
	    return ResponseEntity.ok(snapshot);
	}
}
