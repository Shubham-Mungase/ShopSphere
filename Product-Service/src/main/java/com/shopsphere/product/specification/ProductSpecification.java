package com.shopsphere.product.specification;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.shopsphere.product.entity.Product;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {

    public static Specification<Product> search(
            String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            boolean active
    ) {
        return (root, query, cb) -> {

            Predicate predicate = cb.conjunction();

            if (keyword != null && !keyword.isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                predicate = cb.and(
                        predicate,
                        cb.or(
                                cb.like(cb.lower(root.get("name")), likePattern),
                                cb.like(cb.lower(root.get("description")), likePattern)
                        )
                );
            }

            if (minPrice != null) {
                predicate = cb.and(
                        predicate,
                        cb.greaterThanOrEqualTo(root.get("price"), minPrice)
                );
            }

            if (maxPrice != null) {
                predicate = cb.and(
                        predicate,
                        cb.lessThanOrEqualTo(root.get("price"), maxPrice)
                );
            }

            if (active) {
                predicate = cb.and(
                        predicate,
                        cb.isTrue(root.get("active"))
                );
            }

            return predicate;
        };
    }

    public static Specification<Product> byCategoryId(UUID id) {
        return (root, query, cb) -> {

            Predicate predicate = cb.conjunction();

            if (id != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("category").get("id"), id)
                );
            }

            return predicate;
        };
    }
}
