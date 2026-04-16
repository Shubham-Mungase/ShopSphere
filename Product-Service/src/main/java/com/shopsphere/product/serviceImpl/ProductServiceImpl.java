package com.shopsphere.product.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.product.dto.PageResponse;
import com.shopsphere.product.dto.ProductRequestDto;
import com.shopsphere.product.dto.ProductResponseDto;
import com.shopsphere.product.dto.ProductSearchRequest;
import com.shopsphere.product.dto.ProductSearchResponse;
import com.shopsphere.product.entity.Category;
import com.shopsphere.product.entity.OutboxEntity;
import com.shopsphere.product.entity.Product;
import com.shopsphere.product.events.publisher.ProductCreatedEvent;
import com.shopsphere.product.events.publisher.ProductEventPublisher;
import com.shopsphere.product.exception.InvalidDataException;
import com.shopsphere.product.exception.ResourceNotFoundException;
import com.shopsphere.product.repo.CategoryRepository;
import com.shopsphere.product.repo.OutboxRepository;
import com.shopsphere.product.repo.ProductRepository;
import com.shopsphere.product.service.ProductService;
import com.shopsphere.product.specification.ProductSpecification;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

	private final ProductRepository prodRepo;
	private final CategoryRepository catRepo;
	
	private final ObjectMapper objectMapper;
	private final ProductEventPublisher eventPublisher;
	private final OutboxRepository outboxRepository;


    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

	



	public ProductServiceImpl(ProductRepository prodRepo, CategoryRepository catRepo, ObjectMapper objectMapper,
			ProductEventPublisher eventPublisher, OutboxRepository outboxRepository) {
		super();
		this.prodRepo = prodRepo;
		this.catRepo = catRepo;
		this.objectMapper = objectMapper;
		this.eventPublisher = eventPublisher;
		this.outboxRepository = outboxRepository;
	}

	@Override
	@CacheEvict(value = { "productById", "productsByCategory" }, allEntries = true)
	public ProductResponseDto createProduct(ProductRequestDto req) {

		 log.info("Creating product with name={}", req.getName());

	        if (req.getName() == null || req.getName().isBlank()) {
	            log.warn("Invalid product name");
	            throw new InvalidDataException("Product name cannot be empty");
	        }
		
	        Category category = catRepo.findById(req.getCategoryId())
	                .orElseThrow(() -> {
	                    log.error("Invalid categoryId={}", req.getCategoryId());
	                    return new ResourceNotFoundException("Category Not Found");
	                });
		Product product = new Product();
		product.setActive(true);
		product.setCategory(category);
		product.setDescription(req.getDescription());
		product.setPrice(req.getPrice());
		product.setName(req.getName());
		
		Product save = prodRepo.save(product);
		  log.info("Product created successfully with id={}", save.getId());

		//mapping event to kafka
		ProductCreatedEvent createdEvent=new ProductCreatedEvent();
		createdEvent.setProductId(save.getId());
		createdEvent.setProductName(save.getName());
		createdEvent.setEventType("PRODUCT_CREATED");
		
		
		try {
			 String payload = objectMapper.writeValueAsString(createdEvent);
	         
			 
			 OutboxEntity outbox = new OutboxEntity();

				outbox.setAggregateType("PRODUCT");
				outbox.setAggregateId(save.getId().toString());
				outbox.setEventType("PRODUCT_CREATED");
				outbox.setPayload(payload);
				outbox.setStatus("NEW");
				outbox.setCreatedAt(LocalDateTime.now());

				outboxRepository.save(outbox);
				 log.info("ProductCreatedEvent Saved for productId={}", save.getId());
			        
			
		} catch (Exception e) {
			 log.error("Failed to serialize ProductCreatedEvent for productId={}", save.getId(), e);
	            throw new RuntimeException("Event Publishing Failed");
		}
		
		
		
		
		
		return mapToDto(save);
	}

	@Override
	@Cacheable(value = "productById", key = "#productId")
	public ProductResponseDto getProduct(UUID productId) {

		  log.info("Fetching product by id={}", productId);
		Product product = prodRepo.findById(productId).orElseThrow(() -> new RuntimeException("Product Not Found"));

		return mapToDto(product);
	}

	@Override
	@Cacheable(value = "productsByCategory", key = "#categoryId")
	public List<ProductResponseDto> getAllProductsByCategory(UUID categoryId) {
		  log.info("Fetching products for categoryId={}", categoryId);

	        if (!catRepo.existsById(categoryId)) {
	            log.error("Category not found id={}", categoryId);
	            throw new ResourceNotFoundException("Category Not Found");
	        }
		
		return prodRepo.findByCategoryIdAndActiveTrue(categoryId).stream().map(this::mapToDto).toList();
	}

	@Override
	@Caching(evict = { @CacheEvict(value = "productById", key = "#productId"),
			@CacheEvict(value = "productsByCategory", allEntries = true) })
	public ProductResponseDto updateProduct(UUID productId, ProductRequestDto req) {

		log.info("Updating product id={}", productId);

        Product product = prodRepo.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found id={}", productId);
                    return new ResourceNotFoundException("Product Not Found");
                });
		
		product.setName(req.getName());
		product.setPrice(req.getPrice());
		product.setDescription(req.getDescription());
		 log.info("Product updated successfully id={}", productId);

		return mapToDto(prodRepo.save(product));
	}

	@Override
	@Caching(evict = { @CacheEvict(value = "productById", key = "#productId"),
			@CacheEvict(value = "productsByCategory", allEntries = true) })
	public boolean deactivateProduct(UUID productId) {

		 log.info("Deactivating product id={}", productId);

	        Product product = prodRepo.findById(productId)
	                .orElseThrow(() -> {
	                    log.error("Product not found id={}", productId);
	                    return new ResourceNotFoundException("Product Not Found");
	                });

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
		
		  log.info("Searching products with keyword={}, categoryId={}", request.getKeyword(), request.getCategoryId());


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

			    log.info("Search result count={} page={}", page.getTotalElements(), page.getNumber());

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
