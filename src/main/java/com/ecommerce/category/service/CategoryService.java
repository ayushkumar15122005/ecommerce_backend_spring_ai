package com.ecommerce.category.service;

import com.ecommerce.category.dto.CategoryDTO;
import com.ecommerce.category.entity.Category;
import com.ecommerce.category.mapper.CategoryMapper;
import com.ecommerce.category.repository.CategoryRepository;
import com.ecommerce.common.exception.BadRequestException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        return categoryMapper.toDTO(findOrThrow(id));
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("A category with this name already exists");
        }
        Category saved = categoryRepository.save(categoryMapper.toEntity(request));
        return categoryMapper.toDTO(saved);
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO request) {
        Category category = findOrThrow(id);

        if (!category.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("A category with this name already exists");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = findOrThrow(id);
        // products.category_id is ON DELETE RESTRICT at the DB level, so this will
        // naturally fail with a clear DB constraint error if products still reference it.
        categoryRepository.delete(category);
    }

    private Category findOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Category", id));
    }
}
