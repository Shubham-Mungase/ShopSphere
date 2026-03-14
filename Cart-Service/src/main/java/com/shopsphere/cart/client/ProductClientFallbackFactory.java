package com.shopsphere.cart.client;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.shopsphere.cart.dto.client.ProductResponse;

@Component
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {

    @Override
    public ProductClient create(Throwable cause) {

        return productId -> {

            System.out.println("Product service failed: " + cause.getMessage());

            ProductResponse product = new ProductResponse();
            product.setId(productId);
            product.setName("Fallback Product");
            product.setPrice(BigDecimal.ZERO);

            return product;
        };
    }
}