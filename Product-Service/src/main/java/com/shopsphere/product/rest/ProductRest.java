package com.shopsphere.product.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.product.dto.PageResponse;
import com.shopsphere.product.dto.ProductRequestDto;
import com.shopsphere.product.dto.ProductResponseDto;
import com.shopsphere.product.dto.ProductSearchRequest;
import com.shopsphere.product.dto.ProductSearchResponse;
import com.shopsphere.product.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/product")
public class ProductRest {

	 private final ProductService service;
	 


	public ProductRest(ProductService service) {
		super();
		this.service = service;
	}
	 
	@PostMapping
	public ResponseEntity<?> createProduct(@RequestBody @Valid ProductRequestDto dto)
	{
		ProductResponseDto product = service.createProduct(dto);
		
		try {
			if(product!=null)
				return new ResponseEntity<ProductResponseDto>(product, HttpStatus.CREATED);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  new ResponseEntity<ProductResponseDto >(product, HttpStatus.INTERNAL_SERVER_ERROR);
	
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateProduct(@PathVariable UUID id, @RequestBody @Valid ProductRequestDto dto)
	{
		ProductResponseDto product = service.updateProduct(id, dto);
		
		try {
			if(product!=null)
				return new ResponseEntity<ProductResponseDto>(product, HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  new ResponseEntity<ProductResponseDto >(product, HttpStatus.INTERNAL_SERVER_ERROR);
	
	}
	
	@GetMapping("/{id}/category")
	public ResponseEntity<?> getAllByCategory(@PathVariable UUID id)
	{
		List<ProductResponseDto> product = service.getAllProductsByCategory(id);
		
		try {
			if(product!=null)
				return new ResponseEntity<List<ProductResponseDto>>(product, HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  new ResponseEntity<List<ProductResponseDto>>(product, HttpStatus.INTERNAL_SERVER_ERROR);
	
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getProduct(@PathVariable UUID id)
	{
		ProductResponseDto product = service.getProduct(id);
		
		try {
			if(product!=null)
				return new ResponseEntity<ProductResponseDto>(product, HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  new ResponseEntity<ProductResponseDto>(product, HttpStatus.INTERNAL_SERVER_ERROR);
	
	}
	@GetMapping("/search")
	public ResponseEntity<?> search(@RequestBody ProductSearchRequest request)
	{
 PageResponse<ProductSearchResponse> product = service.searchProduct(request);		
		try {
			if(product!=null)
				return new ResponseEntity<>(product, HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  new ResponseEntity<>(product, HttpStatus.INTERNAL_SERVER_ERROR);
	
	}
	
	
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deactivateProduct(@PathVariable UUID id)
	{
		boolean product = service.deactivateProduct(id);
		
		try {
			if(product)
				return new ResponseEntity<Boolean>(product, HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  new ResponseEntity<Boolean>(product, HttpStatus.INTERNAL_SERVER_ERROR);
	
	}
	
	

	 
}
