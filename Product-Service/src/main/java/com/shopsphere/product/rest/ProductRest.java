package com.shopsphere.product.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.product.dto.ApiResponse;
import com.shopsphere.product.dto.PageResponse;
import com.shopsphere.product.dto.ProductRequestDto;
import com.shopsphere.product.dto.ProductResponseDto;
import com.shopsphere.product.dto.ProductSearchRequest;
import com.shopsphere.product.dto.ProductSearchResponse;
import com.shopsphere.product.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/product")
public class ProductRest {

    private final ProductService service;

    public ProductRest(ProductService service) {
        this.service = service;
    }

    //  ADMIN ONLY → Create Product
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(
            @RequestBody @Valid ProductRequestDto dto) {

        ProductResponseDto product = service.createProduct(dto);

        return ResponseEntity.status(201)
                .body(new ApiResponse<>(true, "Product created successfully", product));
    }

    //  ADMIN ONLY → Update Product
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(
            @PathVariable UUID id,
            @RequestBody @Valid ProductRequestDto dto) {

        ProductResponseDto product = service.updateProduct(id, dto);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product updated successfully", product)
        );
    }

    //  USER + ADMIN → Get products by category
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}/category")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getAllByCategory(
            @PathVariable UUID id) {

        List<ProductResponseDto> products =
                service.getAllProductsByCategory(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Products fetched successfully", products)
        );
    }

    // feign client will call this method  USER + ADMIN → Get product by ID
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProduct(
            @PathVariable UUID id) {

        ProductResponseDto product = service.getProduct(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product fetched successfully", product)
        );
    }

    // USER + ADMIN → Search Products
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ProductSearchResponse>>> search(
            @RequestBody ProductSearchRequest request) {

        PageResponse<ProductSearchResponse> result =
                service.searchProduct(request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Search results fetched", result)
        );
    }

    // 🔒 ADMIN ONLY → Deactivate Product
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateProduct(
            @PathVariable UUID id) {

        service.deactivateProduct(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product deactivated successfully", null)
        );
    }
}