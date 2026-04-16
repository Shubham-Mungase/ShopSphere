package com.shopsphere.product.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.shopsphere.product.dto.ProductSnapshotDto;
import com.shopsphere.product.entity.Product;
import com.shopsphere.product.entity.ProductImage;
import com.shopsphere.product.entity.ProductSnapshotEntity;
import com.shopsphere.product.exception.InvalidDataException;
import com.shopsphere.product.exception.ResourceNotFoundException;
import com.shopsphere.product.repo.ProductImageRepository;
import com.shopsphere.product.repo.ProductRepository;
import com.shopsphere.product.repo.ProductSnapshotRepository;
import com.shopsphere.product.service.ProductSnapShotService;

@Service
public class ProductSnapShotServiceImpl implements ProductSnapShotService {

    private static final Logger log = LoggerFactory.getLogger(ProductSnapShotServiceImpl.class);

    private final ProductSnapshotRepository snapshotRepo;
    private final ProductRepository prodRepo;
    private final ProductImageRepository prodImgRepo;

    public ProductSnapShotServiceImpl(ProductSnapshotRepository snapshotRepo,
                                      ProductRepository prodRepo,
                                      ProductImageRepository prodImgRepo) {
        this.snapshotRepo = snapshotRepo;
        this.prodRepo = prodRepo;
        this.prodImgRepo = prodImgRepo;
    }

    @Override
    public ProductSnapshotDto createSnapshot(UUID productId) {

        log.info("Creating snapshot for productId={}", productId);

        // 🔴 Product validation
        Product product = prodRepo.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found id={}", productId);
                    return new ResourceNotFoundException("Product not found with id: " + productId);
                });

        // 🔴 Image validation
        List<ProductImage> images = prodImgRepo.findByProductId(productId);

        if (images == null || images.isEmpty()) {
            log.error("No images found for productId={}", productId);
            throw new ResourceNotFoundException("Product images not found for productId: " + productId);
        }

        ProductImage firstImage = images.get(0);

        // 🔴 Snapshot creation
        ProductSnapshotEntity entity = new ProductSnapshotEntity();

        entity.setCategoryId(product.getCategory().getId().toString());
        entity.setProductId(product.getId().toString());
        entity.setActive(product.isActive());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setDescription(product.getDescription());
        entity.setProductName(product.getName());
        entity.setImageUrl(firstImage.getImageUrl());
        entity.setPrice(product.getPrice());

        // 🔥 Discount calculation
        BigDecimal price = product.getPrice();

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid product price for productId={}", productId);
            throw new InvalidDataException("Invalid product price");
        }

        BigDecimal discount = calculateDiscount(product);
        BigDecimal finalPrice = price.subtract(discount);

        entity.setDiscount(discount);
        entity.setFinalPrice(finalPrice);

        ProductSnapshotEntity saved = snapshotRepo.save(entity);

        log.info("Snapshot created successfully for productId={} snapshotId={}",
                productId, saved.getProductId());

        return mapToDto(saved);
    }

    // 🔹 Discount Logic
    private BigDecimal calculateDiscount(Product product) {

        BigDecimal price = product.getPrice();

        BigDecimal discount = price
                .multiply(BigDecimal.valueOf(10))
                .divide(BigDecimal.valueOf(100));

        log.debug("Calculated discount={} for productId={}", discount, product.getId());

        return discount;
    }

    // 🔹 Mapper
    private ProductSnapshotDto mapToDto(ProductSnapshotEntity entity) {

        ProductSnapshotDto dto = new ProductSnapshotDto();
        dto.setDiscount(entity.getDiscount());
        dto.setPrice(entity.getPrice());
        dto.setProductId(entity.getProductId());
        dto.setFinalPrice(entity.getFinalPrice());
        dto.setImageUrl(entity.getImageUrl());
        dto.setProductName(entity.getProductName());

        return dto;
    }
}