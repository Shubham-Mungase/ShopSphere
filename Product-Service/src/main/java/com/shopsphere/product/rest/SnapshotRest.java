package com.shopsphere.product.rest;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.product.dto.ProductSnapshotDto;
import com.shopsphere.product.service.ProductSnapShotService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/product")
public class SnapshotRest {

	private final ProductSnapShotService service;

	public SnapshotRest(ProductSnapShotService service) {
		this.service = service;
	}

	private static final Logger log = LoggerFactory.getLogger(SnapshotRest.class);

	// ONLY ADMIN (or internal calls)

	@GetMapping("/snapshot/{productId}")
	public ResponseEntity<ProductSnapshotDto> createSnapshot(@PathVariable UUID productId, HttpServletRequest request) {

		log.info("Snapshot API HIT for productId={}", productId);

		ProductSnapshotDto snapshot = service.createSnapshot(productId);
		return ResponseEntity.ok(snapshot);
	}
}