package com.shopsphere.product.serviceImpl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.shopsphere.product.dto.ProductImageRequestDto;
import com.shopsphere.product.dto.ProductImageResponseDto;
import com.shopsphere.product.entity.Product;
import com.shopsphere.product.entity.ProductImage;
import com.shopsphere.product.exception.InvalidDataException;
import com.shopsphere.product.exception.ResourceNotFoundException;
import com.shopsphere.product.repo.ProductImageRepository;
import com.shopsphere.product.repo.ProductRepository;
import com.shopsphere.product.service.ProductImageService;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    private static final Logger log = LoggerFactory.getLogger(ProductImageServiceImpl.class);

    private final ProductImageRepository imgRepo;
    private final ProductRepository prodRepo;

    public ProductImageServiceImpl(ProductImageRepository imgRepo, ProductRepository prodRepo) {
        this.imgRepo = imgRepo;
        this.prodRepo = prodRepo;
    }

    // ✅ ADD IMAGE
    @Override
    @CacheEvict(value = "productImagesByProduct", key = "#productId")
    public ProductImageResponseDto addProductImage(UUID productId, ProductImageRequestDto req) {

        log.info("Adding image for productId={}", productId);

        // 🔴 Validate request
        if (req.getImageUrl() == null || req.getImageUrl().isBlank()) {
            log.warn("Invalid image URL for productId={}", productId);
            throw new InvalidDataException("Image URL cannot be empty");
        }

        Product product = prodRepo.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found for productId={}", productId);
                    return new ResourceNotFoundException("Product Not Found with id: " + productId);
                });

        ProductImage image = new ProductImage();
        image.setImageUrl(req.getImageUrl());
        image.setProduct(product);

        ProductImage saved = imgRepo.save(image);

        log.info("Image added successfully with id={} for productId={}", saved.getId(), productId);

        return mapToDto(saved);
    }

    // ✅ GET IMAGES
    @Override
    @Cacheable(value = "productImagesByProduct", key = "#productId")
    public List<ProductImageResponseDto> getImagesByProduct(UUID productId) {

        log.info("Fetching images for productId={}", productId);

        if (!prodRepo.existsById(productId)) {
            log.error("Product not found while fetching images for productId={}", productId);
            throw new ResourceNotFoundException("Product Not Found with id: " + productId);
        }

        List<ProductImageResponseDto> images = imgRepo.findByProductId(productId)
                .stream()
                .map(this::mapToDto)
                .toList();

        log.info("Total images fetched={} for productId={}", images.size(), productId);

        return images;
    }

    // ✅ DELETE IMAGE
    @Override
    @Caching(evict = {
            @CacheEvict(value = "productImagesByProduct", allEntries = true)
    })
    public boolean deleteImage(UUID imageId) {

        log.info("Deleting image with id={}", imageId);

        ProductImage image = imgRepo.findById(imageId)
                .orElseThrow(() -> {
                    log.error("Image not found with id={}", imageId);
                    return new ResourceNotFoundException("Image Not Found with id: " + imageId);
                });

        imgRepo.delete(image);

        log.info("Image deleted successfully with id={}", imageId);

        return true;
    }

    // 🔹 Mapper
    private ProductImageResponseDto mapToDto(ProductImage img) {
        ProductImageResponseDto dto = new ProductImageResponseDto();
        dto.setId(img.getId());
        dto.setImageUrl(img.getImageUrl());
        dto.setCreatedAt(img.getCreatedAt());
        dto.setUpdatedAt(img.getUpdatedAt());
        dto.setProductId(img.getProduct().getId());
        return dto;
    }
}