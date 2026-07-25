package com.ecommerce.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal discountPercent;
    private BigDecimal finalPrice;
    private String brand;
    private Integer stock;
    private BigDecimal rating;
    private String categoryName;
    private String primaryImageUrl;
}
