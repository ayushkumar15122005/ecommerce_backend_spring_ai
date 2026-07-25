package com.ecommerce.product.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must not be negative")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Discount percent must not be negative")
    @DecimalMax(value = "100.0", message = "Discount percent must not exceed 100")
    private BigDecimal discountPercent;

    private String brand;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must not be negative")
    private Integer stock;

    @NotNull(message = "Category is required")
    private Long categoryId;

    /** comma-separated tags, e.g. "wireless,bluetooth" */
    private String tags;

    private List<String> imageUrls;
}
