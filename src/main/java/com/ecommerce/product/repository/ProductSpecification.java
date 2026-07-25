package com.ecommerce.product.repository;

import com.ecommerce.product.dto.ProductSearchRequest;
import com.ecommerce.product.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Builds a JPA Specification from a ProductSearchRequest so ProductService can
 * combine keyword search, category/brand filters, and price range in a single
 * dynamic query instead of writing one @Query per filter combination.
 */
public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> fromSearchRequest(ProductSearchRequest request) {
        return Specification
                .where(hasKeyword(request.getKeyword()))
                .and(hasCategory(request.getCategoryId()))
                .and(hasBrand(request.getBrand()))
                .and(minPrice(request.getMinPrice()))
                .and(maxPrice(request.getMaxPrice()));
    }

    public static Specification<Product> hasKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        String likePattern = "%" + keyword.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), likePattern),
                cb.like(cb.lower(root.get("brand")), likePattern),
                cb.like(cb.lower(root.get("tags")), likePattern)
        );
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> hasBrand(String brand) {
        if (brand == null || brand.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(cb.lower(root.get("brand")), brand.toLowerCase());
    }

    public static Specification<Product> minPrice(BigDecimal minPrice) {
        if (minPrice == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> maxPrice(BigDecimal maxPrice) {
        if (maxPrice == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }
}
