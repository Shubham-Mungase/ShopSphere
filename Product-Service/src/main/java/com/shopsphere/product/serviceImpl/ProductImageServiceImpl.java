package com.shopsphere.product.serviceImpl;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.shopsphere.product.dto.ProductImageRequestDto;
import com.shopsphere.product.dto.ProductImageResponseDto;
import com.shopsphere.product.entity.Product;
import com.shopsphere.product.entity.ProductImage;
import com.shopsphere.product.repo.ProductImageRepository;
import com.shopsphere.product.repo.ProductRepository;
import com.shopsphere.product.service.ProductImageService;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository imgRepo;
    private final ProductRepository prodRepo;

    public ProductImageServiceImpl(ProductImageRepository imgRepo, ProductRepository prodRepo) {
        this.imgRepo = imgRepo;
        this.prodRepo = prodRepo;
    }

    @Override
    @CacheEvict(value = "productImagesByProduct", key = "#productId")
    public ProductImageResponseDto addProductImage(UUID productId, ProductImageRequestDto req) {

        Product product = prodRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));

        ProductImage image = new ProductImage();
        image.setImageUrl(req.getImageUrl());
        image.setProduct(product);

        ProductImage saved = imgRepo.save(image);

        ProductImageResponseDto dto = new ProductImageResponseDto();
        dto.setId(saved.getId());
        dto.setImageUrl(saved.getImageUrl());
        dto.setCreatedAt(saved.getCreatedAt());
        dto.setUpdatedAt(saved.getUpdatedAt());
        dto.setProductId(saved.getProduct().getId());

        return dto;
    }

    @Override
    @Cacheable(value = "productImagesByProduct", key = "#productId")
    public List<ProductImageResponseDto> getImagesByProduct(UUID productId) {

        return imgRepo.findByProductId(productId)
                .stream()
                .map(img -> {
                    ProductImageResponseDto dto = new ProductImageResponseDto();
                    dto.setId(img.getId());
                    dto.setImageUrl(img.getImageUrl());
                    dto.setCreatedAt(img.getCreatedAt());
                    dto.setUpdatedAt(img.getUpdatedAt());
                    dto.setProductId(img.getProduct().getId());
                    return dto;
                })
                .toList();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "productImagesByProduct", allEntries = true)
    })
    public boolean deleteImage(UUID imageId) {

        imgRepo.deleteById(imageId);
        return true;
    }
}
