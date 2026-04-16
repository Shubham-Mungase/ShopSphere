package com.shopsphere.product.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/product/{productId}/image")
public class ProductImageRest {

    private final ProductImageService service;

    public ProductImageRest(ProductImageService service) {
        this.service = service;
    }

    // ONLY ADMIN can add images
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductImageResponseDto> addImage(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductImageRequestDto dto) {

        ProductImageResponseDto productImage = service.addProductImage(productId, dto);
        return new ResponseEntity<>(productImage, HttpStatus.CREATED);
    }

    //  ADMIN + USER can view images
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public ResponseEntity<List<ProductImageResponseDto>> getImagesById(@PathVariable UUID productId) {
        return ResponseEntity.ok(service.getImagesByProduct(productId));
    }

    //  ONLY ADMIN can delete images
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<Boolean> deleteImage(@PathVariable UUID productId) {

        boolean deleted = service.deleteImage(productId);
        return deleted 
                ? ResponseEntity.ok(true)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }
}