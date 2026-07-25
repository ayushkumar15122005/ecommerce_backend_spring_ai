package com.ecommerce.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPercent;
    private BigDecimal finalPrice;
    private String brand;
    private Integer stock;
    private BigDecimal rating;
    private Long categoryId;
    private String categoryName;
    private List<String> tags;
    private List<String> imageUrls;
}
