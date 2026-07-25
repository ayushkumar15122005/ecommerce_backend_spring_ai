package com.ecommerce.category.controller;

import com.ecommerce.category.dto.CategoryDTO;
import com.ecommerce.category.service.CategoryService;
import com.ecommerce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryDTO>> getAllCategories() {
        return ApiResponse.success(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryDTO> getCategory(@PathVariable Long id) {
        return ApiResponse.success(categoryService.getCategoryById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO request) {
        return ApiResponse.success("Category created", categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO request) {
        return ApiResponse.success("Category updated", categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success("Category deleted", null);
    }
}
