package com.shopsphere.product.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.product.dto.CategoryRequestDto;
import com.shopsphere.product.dto.CategoryResponseDto;
import com.shopsphere.product.dto.CategoryTreeDto;
import com.shopsphere.product.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/product/category")
public class CategoryRest {

	
	
	private final CategoryService categoryService;

	public CategoryRest( CategoryService categoryService) {
		super();
		this.categoryService = categoryService;
	}
	
	@PostMapping
	public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody @Valid CategoryRequestDto req)
	{
		CategoryResponseDto category = categoryService.createCategory(req);
		try {
			if(category!=null)
				return new ResponseEntity<CategoryResponseDto>(category, HttpStatus.CREATED);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  new ResponseEntity<CategoryResponseDto>(category, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping
	public ResponseEntity<List<CategoryResponseDto>> getAll()
	{
		List<CategoryResponseDto> allCategories = categoryService.getAllCategories();
		try {
			if(allCategories!=null)
				return new ResponseEntity<List<CategoryResponseDto>>(allCategories, HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  new ResponseEntity<List<CategoryResponseDto>>(allCategories, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<CategoryResponseDto> getById(@PathVariable UUID id)
	{
			CategoryResponseDto category = categoryService.getCategory(id);
		try {
			if(category!=null)
				return new ResponseEntity<CategoryResponseDto>(category, HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  new ResponseEntity<CategoryResponseDto>(category, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	 @GetMapping("/tree")
	    public ResponseEntity<List<CategoryTreeDto>> getCategoryTree() {
	        return ResponseEntity.ok(categoryService.getCategoryChildren());
	    }
	
	
	
}
