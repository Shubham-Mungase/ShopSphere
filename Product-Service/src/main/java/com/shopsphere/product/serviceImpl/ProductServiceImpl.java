package com.shopsphere.product.serviceImpl;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.shopsphere.product.dto.PageResponse;
import com.shopsphere.product.dto.ProductRequestDto;
import com.shopsphere.product.dto.ProductResponseDto;
import com.shopsphere.product.dto.ProductSearchRequest;
import com.shopsphere.product.dto.ProductSearchResponse;
import com.shopsphere.product.entity.Category;
import com.shopsphere.product.entity.Product;
import com.shopsphere.product.repo.CategoryRepository;
import com.shopsphere.product.repo.ProductRepository;
import com.shopsphere.product.service.ProductService;
import com.shopsphere.product.specification.ProductSpecification;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository prodRepo;
	private final CategoryRepository catRepo;

	public ProductServiceImpl(ProductRepository prodRepo, CategoryRepository catRepo) {
		this.prodRepo = prodRepo;
		this.catRepo = catRepo;
	}

	@Override
	@CacheEvict(value = { "productById", "productsByCategory" }, allEntries = true)
	public ProductResponseDto createProduct(ProductRequestDto req) {

		Category category = catRepo.findById(req.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Invalid Category"));

		Product product = new Product();
		product.setActive(true);
		product.setCategory(category);
		product.setDescription(req.getDescription());
		product.setPrice(req.getPrice());
		product.setName(req.getName());

		return mapToDto(prodRepo.save(product));
	}

	@Override
	@Cacheable(value = "productById", key = "#productId")
	public ProductResponseDto getProduct(UUID productId) {

		Product product = prodRepo.findById(productId).orElseThrow(() -> new RuntimeException("Product Not Found"));

		return mapToDto(product);
	}

	@Override
	@Cacheable(value = "productsByCategory", key = "#categoryId")
	public List<ProductResponseDto> getAllProductsByCategory(UUID categoryId) {

		return prodRepo.findByCategoryIdAndActiveTrue(categoryId).stream().map(this::mapToDto).toList();
	}

	@Override
	@Caching(evict = { @CacheEvict(value = "productById", key = "#productId"),
			@CacheEvict(value = "productsByCategory", allEntries = true) })
	public ProductResponseDto updateProduct(UUID productId, ProductRequestDto req) {

		Product product = prodRepo.findById(productId).orElseThrow(() -> new RuntimeException("Product Not Found"));

		product.setName(req.getName());
		product.setPrice(req.getPrice());
		product.setDescription(req.getDescription());

		return mapToDto(prodRepo.save(product));
	}

	@Override
	@Caching(evict = { @CacheEvict(value = "productById", key = "#productId"),
			@CacheEvict(value = "productsByCategory", allEntries = true) })
	public boolean deactivateProduct(UUID productId) {

		Product product = prodRepo.findById(productId).orElseThrow(() -> new RuntimeException("Product Not Found"));

		product.setActive(false);
		prodRepo.save(product);
		return true;
	}

	private ProductResponseDto mapToDto(Product product) {
		ProductResponseDto dto = new ProductResponseDto();
		dto.setId(product.getId());
		dto.setName(product.getName());
		dto.setDescription(product.getDescription());
		dto.setPrice(product.getPrice());
		dto.setActive(product.isActive());
		Category cat = product.getCategory();
		if (cat != null) {
			dto.setCategory(cat.getId());
			dto.setCategoryName(cat.getName());
		}

		return dto;
	}

	@Cacheable(
			   value = "productSearch",
			   key = "#request.keyword + '-' + #request.categoryId + '-' + #request.minPrice + '-' + #request.maxPrice + '-' + #request.page + '-' + #request.size"
			)
			public PageResponse<ProductSearchResponse> searchProduct(ProductSearchRequest request) {

			    Specification<Product> specification =
			            ProductSpecification.search(request.getKeyword(), request.getMinPrice(),
			                    request.getMaxPrice(), true);

			    if (request.getCategoryId() != null) {
			        specification = specification.and(
			                ProductSpecification.byCategoryId(request.getCategoryId()));
			    }

			    Sort sort = Sort.by("createdAt").descending();
			    if ("price".equalsIgnoreCase(request.getSortBy())) {
			        sort = "desc".equalsIgnoreCase(request.getSortOrder())
			                ? Sort.by("price").descending()
			                : Sort.by("price").ascending();
			    }

			    Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

			    Page<ProductSearchResponse> page =
			            prodRepo.findAll(specification, pageable).map(this::mapToSearchDto);

			    PageResponse<ProductSearchResponse> response = new PageResponse<>();
			    response.setContent(page.getContent());
			    response.setPage(page.getNumber());
			    response.setSize(page.getSize());
			    response.setTotalElements(page.getTotalElements());
			    response.setTotalPages(page.getTotalPages());
			    return response;
			}

	private ProductSearchResponse mapToSearchDto(Product product) {

		ProductSearchResponse dto = new ProductSearchResponse();
		dto.setId(product.getId());
		dto.setName(product.getName());
		dto.setPrice(product.getPrice());
		dto.setDescription(product.getDescription());
		dto.setActive(product.isActive());
		dto.setCategory(product.getCategory().getId());
		return dto;
	}

}
