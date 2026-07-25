package com.ecommerce.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductSearchRequest {

    private String keyword;          // matches against product name
    private Long categoryId;
    private String brand;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    /** one of: price_asc, price_desc, rating_desc, newest (defaults to newest) */
    private String sortBy = "newest";

    private int page = 0;
    private int size = 12;
}
