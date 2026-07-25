package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.ProductDTO;
import com.ecommerce.product.dto.ProductDetailDTO;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .discountPercent(product.getDiscountPercent())
                .finalPrice(product.getFinalPrice())
                .brand(product.getBrand())
                .stock(product.getStock())
                .rating(product.getRating())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .primaryImageUrl(findPrimaryImageUrl(product))
                .build();
    }

    public ProductDetailDTO toDetailDTO(Product product) {
        return ProductDetailDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPercent(product.getDiscountPercent())
                .finalPrice(product.getFinalPrice())
                .brand(product.getBrand())
                .stock(product.getStock())
                .rating(product.getRating())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .tags(parseTags(product.getTags()))
                .imageUrls(product.getImages().stream().map(ProductImage::getImageUrl).toList())
                .build();
    }

    private String findPrimaryImageUrl(Product product) {
        if (product.getImages() == null || product.getImages().isEmpty()) {
            return null;
        }
        return product.getImages().stream()
                .filter(ProductImage::isPrimary)
                .findFirst()
                .orElse(product.getImages().get(0))
                .getImageUrl();
    }

    private List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .toList();
    }
}
