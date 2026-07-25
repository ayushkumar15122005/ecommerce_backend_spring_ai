package com.ecommerce.product.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class UpdateProductRequest {

    @Size(max = 200, message = "Product name must not exceed 200 characters")
    private String name;

    private String description;

    @DecimalMin(value = "0.0", message = "Price must not be negative")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Discount percent must not be negative")
    @DecimalMax(value = "100.0", message = "Discount percent must not exceed 100")
    private BigDecimal discountPercent;

    private String brand;

    @Min(value = 0, message = "Stock must not be negative")
    private Integer stock;

    private Long categoryId;

    private String tags;

    private List<String> imageUrls;
}
