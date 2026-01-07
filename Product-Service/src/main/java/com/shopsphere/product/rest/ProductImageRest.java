package com.shopsphere.product.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.product.dto.ProductImageRequestDto;
import com.shopsphere.product.dto.ProductImageResponseDto;
import com.shopsphere.product.service.ProductImageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("product/{productId}/image")
public class ProductImageRest {

	private final ProductImageService service;

	public ProductImageRest(ProductImageService service) {
		super();
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<ProductImageResponseDto> addImage(@PathVariable UUID productId,
			@Valid @RequestBody ProductImageRequestDto dto) {

		ProductImageResponseDto productImage = service.addProductImage(productId, dto);

		try {
			if (productImage != null) {
				return new ResponseEntity<ProductImageResponseDto>(productImage, HttpStatus.CREATED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<ProductImageResponseDto>(productImage, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@GetMapping
	public ResponseEntity<List<ProductImageResponseDto>> getImagesById(@PathVariable UUID productId) {
		List<ProductImageResponseDto> imagesByProduct = service.getImagesByProduct(productId);
		try {
			if (imagesByProduct != null)
				return new ResponseEntity<List<ProductImageResponseDto>>(imagesByProduct, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<List<ProductImageResponseDto>>(imagesByProduct, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@DeleteMapping
	public ResponseEntity<Boolean> deleteImage(@PathVariable UUID productId) {
		
		boolean deleteImage = service.deleteImage(productId);
		
		try {
			if (deleteImage) {
				return new ResponseEntity<Boolean>(HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Boolean>(HttpStatus.INTERNAL_SERVER_ERROR);

	}
}
