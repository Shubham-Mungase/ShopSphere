package com.shopsphere.product.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shopsphere.product.dto.ProductSnapshotDto;
import com.shopsphere.product.entity.Product;
import com.shopsphere.product.entity.ProductImage;
import com.shopsphere.product.entity.ProductSnapshotEntity;
import com.shopsphere.product.repo.ProductImageRepository;
import com.shopsphere.product.repo.ProductRepository;
import com.shopsphere.product.repo.ProductSnapshotRepository;
import com.shopsphere.product.service.ProductSnapShotService;

@Service
public class ProductSnapShotServiceImpl implements ProductSnapShotService {


	private final ProductSnapshotRepository snapshotRepo;
	private final ProductRepository prodRepo;
	private final ProductImageRepository prodImgRepo;
	
	
	
	public ProductSnapShotServiceImpl(ProductSnapshotRepository snapshotRepo, ProductRepository prodRepo,
			ProductImageRepository prodImgRepo) {
		super();
		this.snapshotRepo = snapshotRepo;
		this.prodRepo = prodRepo;
		this.prodImgRepo = prodImgRepo;
		
	}



	@Override
	public ProductSnapshotDto createSnapshot(UUID productId) {
		
		Product product = prodRepo.findById(productId) .orElseThrow(() ->
        new RuntimeException(
                "Product not found with id: " + productId
        )
);
		List<ProductImage> image = prodImgRepo.findByProductId(productId);
		
		ProductSnapshotEntity entity=new ProductSnapshotEntity();
		
		entity.setCategoryId(product.getCategory().getId().toString());
		entity.setProductId(product.getId().toString());
		entity.setActive(product.isActive());
		entity.setCreatedAt(LocalDateTime.now());
		entity.setDescription(product.getDescription());
		entity.setProductName(product.getName());
		entity.setImageUrl(image.getFirst().getImageUrl());
		entity.setPrice(product.getPrice());
		
		BigDecimal price = product.getPrice();
		BigDecimal discount = calculateDiscount(product);
		BigDecimal finalPrice = price.subtract(discount);
		entity.setDiscount(discount);
		entity.setFinalPrice(finalPrice);
		
		ProductSnapshotEntity save = snapshotRepo.save(entity);
		ProductSnapshotDto dto=new ProductSnapshotDto();
		
		dto.setFinalPrice(save.getFinalPrice());
		dto.setImageUrl(save.getImageUrl());
		dto.setProductName(save.getProductName());
		return dto;
	}

	private BigDecimal calculateDiscount(Product product) {

		BigDecimal price = product.getPrice();

		BigDecimal discount = price
		        .multiply(BigDecimal.valueOf(10))
		        .divide(BigDecimal.valueOf(100));
		return discount;
	}

}
