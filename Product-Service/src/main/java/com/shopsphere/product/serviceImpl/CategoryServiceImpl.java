package com.shopsphere.product.serviceImpl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.shopsphere.product.dto.CategoryRequestDto;
import com.shopsphere.product.dto.CategoryResponseDto;
import com.shopsphere.product.dto.CategoryTreeDto;
import com.shopsphere.product.entity.Category;
import com.shopsphere.product.exception.CategoryAlreadyExistsException;
import com.shopsphere.product.exception.ResourceNotFoundException;
import com.shopsphere.product.repo.CategoryRepository;
import com.shopsphere.product.service.CategoryService;
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository catRepo;
    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    public CategoryServiceImpl(CategoryRepository catRepo) {
        this.catRepo = catRepo;
    }

    @Override
    @CacheEvict(value = {"categories", "categoryById", "categoryTree"}, allEntries = true)
    public CategoryResponseDto createCategory(CategoryRequestDto req) {

        log.info("Creating category with name={}", req.getName());

        if (catRepo.findByName(req.getName()).isPresent()) {
            log.warn("Category already exists with name={}", req.getName());
            throw new CategoryAlreadyExistsException("Category already exists with name: " + req.getName());
        }

        Category category = new Category();
        category.setName(req.getName());

        if (req.getParentId() != null) {
        	Category parent = catRepo.findById(req.getParentId())
        	        .orElseThrow(() -> {
        	            log.error("Invalid parent category id={}", req.getParentId());
        	            return new ResourceNotFoundException("Parent Category Not Found");
        	        });
            category.setParent(parent);
        }

        Category saved = catRepo.save(category);

        log.info("Category created successfully with id={}", saved.getId());

        return mapToDto(saved);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {

        log.info("Fetching all categories");

        return catRepo.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Cacheable(value = "categoryById", key = "#categoryId")
    public CategoryResponseDto getCategory(UUID categoryId) {

        log.info("Fetching category by id={}", categoryId);

        Category cat = catRepo.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("Category not found id={}", categoryId);
                    return new ResourceNotFoundException("Category Not Found");
                });

        return mapToDto(cat);
    }

    @Override
    @Cacheable(value = "categoryTree")
    public List<CategoryTreeDto> getCategoryChildren() {

        log.info("Fetching category tree");

        List<Category> parents = catRepo.findByParentIsNull();

        return parents.stream()
                .map(this::mapToTreeDto)
                .toList();
    }

    // 🔹 Helper Methods
    private CategoryResponseDto mapToDto(Category cat) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(cat.getId());
        dto.setName(cat.getName());
        dto.setParentId(cat.getParent() != null ? cat.getParent().getId() : null);
        dto.setCreatedAt(cat.getCreatedAt());
        dto.setUpdatedAt(cat.getUpdatedAt());
        return dto;
    }

    private CategoryTreeDto mapToTreeDto(Category category) {
        CategoryTreeDto dto = new CategoryTreeDto();

        dto.setId(category.getId());
        dto.setName(category.getName());

        if (category.getChildren() != null) {
            dto.setChildren(
                    category.getChildren()
                            .stream()
                            .map(this::mapToTreeDto)
                            .toList()
            );
        }

        return dto;
    }
}