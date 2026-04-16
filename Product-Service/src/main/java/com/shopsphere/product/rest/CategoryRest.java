package com.shopsphere.product.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.product.dto.ApiResponse;
import com.shopsphere.product.dto.CategoryRequestDto;
import com.shopsphere.product.dto.CategoryResponseDto;
import com.shopsphere.product.dto.CategoryTreeDto;
import com.shopsphere.product.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/product/category")
public class CategoryRest {

    private final CategoryService categoryService;

    public CategoryRest(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //  ADMIN ONLY → Create category
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> createCategory(
            @RequestBody @Valid CategoryRequestDto req) {

        CategoryResponseDto category = categoryService.createCategory(req);

        return ResponseEntity.status(201)
                .body(new ApiResponse<>(true, "Category created successfully", category));
    }

    //  USER + ADMIN → Get all categories
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> getAll() {

        List<CategoryResponseDto> categories = categoryService.getAllCategories();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Categories fetched successfully", categories)
        );
    }

    //  USER + ADMIN → Get category by ID
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> getById(
            @PathVariable UUID id) {

        CategoryResponseDto category = categoryService.getCategory(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Category fetched successfully", category)
        );
    }

    //  USER + ADMIN → Category tree
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CategoryTreeDto>>> getCategoryTree() {

        List<CategoryTreeDto> tree = categoryService.getCategoryChildren();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Category tree fetched successfully", tree)
        );
    }
}