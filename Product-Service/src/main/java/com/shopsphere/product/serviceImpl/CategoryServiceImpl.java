package com.shopsphere.product.serviceImpl;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.shopsphere.product.dto.CategoryRequestDto;
import com.shopsphere.product.dto.CategoryResponseDto;
import com.shopsphere.product.dto.CategoryTreeDto;
import com.shopsphere.product.entity.Category;
import com.shopsphere.product.repo.CategoryRepository;
import com.shopsphere.product.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository catRepo;

    public CategoryServiceImpl(CategoryRepository catRepo) {
        this.catRepo = catRepo;
    }

    @Override
    @CacheEvict(value = {"categories", "categoryById"}, allEntries = true)
    public CategoryResponseDto createCategory(CategoryRequestDto req) {

        if (catRepo.findByName(req.getName()).isPresent()) {
            throw new RuntimeException("Category already exists");
        }

        Category category = new Category();
        category.setName(req.getName());

        if (req.getParentId() != null) {
            Category parent = catRepo.findById(req.getParentId())
                    .orElseThrow(() -> new RuntimeException("Invalid Parent Category"));
            category.setParent(parent);
        }

        Category saved = catRepo.save(category);

        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(saved.getId());
        dto.setName(saved.getName());
        dto.setParentId(saved.getParent() != null ? saved.getParent().getId() : null);
        dto.setCreatedAt(saved.getCreatedAt());
        dto.setUpdatedAt(saved.getUpdatedAt());

        return dto;
    }

    @Override
//    @Cacheable(value = "categories" )
    public List<CategoryResponseDto> getAllCategories() {

        return catRepo.findAll().stream().map(cat -> {
            CategoryResponseDto dto = new CategoryResponseDto();
            dto.setId(cat.getId());
            dto.setName(cat.getName());
            dto.setParentId(cat.getParent() != null ? cat.getParent().getId() : null);
            dto.setCreatedAt(cat.getCreatedAt());
            dto.setUpdatedAt(cat.getUpdatedAt());
            return dto;
        }).toList();
    }

    @Override
    @Cacheable(value = "categoryById", key = "#categoryId")
    public CategoryResponseDto getCategory(UUID categoryId) {

        Category cat = catRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category Not Found"));

        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(cat.getId());
        dto.setName(cat.getName());
        dto.setParentId(cat.getParent() != null ? cat.getParent().getId() : null);
        dto.setCreatedAt(cat.getCreatedAt());
        dto.setUpdatedAt(cat.getUpdatedAt());

        return dto;
    }

	@Override
	@Cacheable(value = "categoryTree")
	public List<CategoryTreeDto> getCategoryChildren() {

		List<Category> parents = catRepo.findByParentIsNull();
		
		return parents.stream().map(this::mapToTreeDto).toList();
	}
    
	CategoryTreeDto  mapToTreeDto(Category category)
	{
		CategoryTreeDto dto=new CategoryTreeDto();
		
		dto.setId(category.getId());
		dto.setName(category.getName());
		if(category.getChildren()!=null)
		{
			List<CategoryTreeDto> children = category.getChildren()
					.stream().map(this::mapToTreeDto)
					.toList();
			dto.setChildren(children);
		}
		
		return dto;
		
	}
    
}
