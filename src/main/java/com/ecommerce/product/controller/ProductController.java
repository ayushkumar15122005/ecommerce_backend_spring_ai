package com.ecommerce.product.controller;

import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.common.response.PagedResponse;
import com.ecommerce.product.dto.*;
import com.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<PagedResponse<ProductDTO>> searchProducts(ProductSearchRequest searchRequest) {
        return ApiResponse.success(productService.searchProducts(searchRequest));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDetailDTO> getProduct(@PathVariable Long id) {
        return ApiResponse.success(productService.getProductDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductDetailDTO> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ApiResponse.success("Product created", productService.createProduct(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductDetailDTO> updateProduct(
            @PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        return ApiResponse.success("Product updated", productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success("Product deleted", null);
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductDetailDTO> updateStock(
            @PathVariable Long id, @Valid @RequestBody StockUpdateRequest request) {
        return ApiResponse.success("Stock updated", productService.updateStock(id, request.getStock()));
    }
}
