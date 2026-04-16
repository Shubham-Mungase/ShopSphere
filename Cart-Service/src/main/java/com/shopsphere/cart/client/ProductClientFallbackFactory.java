package com.shopsphere.cart.client;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.shopsphere.cart.dto.client.ApiResponse;
import com.shopsphere.cart.dto.client.ProductResponse;
import com.shopsphere.cart.exceptions.ProductServiceException;

@Component
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {

    private static final Logger log = LoggerFactory.getLogger(ProductClientFallbackFactory.class);

    @Override
    public ProductClient create(Throwable cause) {

    	System.out.println("FALLBACK TRIGGERED: " + cause.getMessage());
        log.error("Product service call failed. Reason: {}", cause.getMessage(), cause);

        return new ProductClient() {

            @Override
            public ApiResponse<ProductResponse> getProduct(UUID productId) {

                log.error("Fallback triggered for getProduct. productId={}", productId);

                // 🔥 OPTION 1 (RECOMMENDED): Fail Fast
                throw new ProductServiceException(
                        "Product Service is unavailable. Please try again later."
                );

                // 🔽 OPTION 2 (NOT RECOMMENDED FOR ORDER FLOW)
                /*
                ProductResponse product = new ProductResponse();
                product.setId(productId);
                product.setName("Unavailable Product");
                product.setPrice(BigDecimal.ZERO);

                log.warn("Returning fallback product for productId={}", productId);

                return product;
                */
            }
        };
    }
}