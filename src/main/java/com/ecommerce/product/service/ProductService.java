package com.ecommerce.product.service;

import com.ecommerce.category.entity.Category;
import com.ecommerce.category.repository.CategoryRepository;
import com.ecommerce.common.exception.BadRequestException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.response.PagedResponse;
import com.ecommerce.product.dto.*;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductImage;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.repository.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public PagedResponse<ProductDTO> searchProducts(ProductSearchRequest request) {
        Specification<Product> spec = ProductSpecification.fromSearchRequest(request);
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), resolveSort(request.getSortBy()));

        var page = productRepository.findAll(spec, pageable).map(productMapper::toDTO);
        return PagedResponse.from(page);
    }

    @Transactional(readOnly = true)
    public ProductDetailDTO getProductDetail(Long id) {
        Product product = productRepository.findWithDetailsById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", id));
        return productMapper.toDetailDTO(product);
    }

    @Transactional
    public ProductDetailDTO createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> ResourceNotFoundException.of("Category", request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .discountPercent(request.getDiscountPercent() != null ? request.getDiscountPercent() : java.math.BigDecimal.ZERO)
                .brand(request.getBrand())
                .stock(request.getStock())
                .category(category)
                .tags(request.getTags())
                .build();

        attachImages(product, request.getImageUrls());

        Product saved = productRepository.save(product);
        return productMapper.toDetailDTO(saved);
    }

    @Transactional
    public ProductDetailDTO updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findWithDetailsById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", id));

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getDiscountPercent() != null) product.setDiscountPercent(request.getDiscountPercent());
        if (request.getBrand() != null) product.setBrand(request.getBrand());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getTags() != null) product.setTags(request.getTags());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Category", request.getCategoryId()));
            product.setCategory(category);
        }

        if (request.getImageUrls() != null) {
            product.getImages().clear();
            attachImages(product, request.getImageUrls());
        }

        Product saved = productRepository.save(product);
        return productMapper.toDetailDTO(saved);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw ResourceNotFoundException.of("Product", id);
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public ProductDetailDTO updateStock(Long id, int newStock) {
        if (newStock < 0) {
            throw new BadRequestException("Stock cannot be negative");
        }
        Product product = productRepository.findWithDetailsById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", id));
        product.setStock(newStock);
        return productMapper.toDetailDTO(productRepository.save(product));
    }

    private void attachImages(Product product, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        List<ProductImage> images = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            images.add(ProductImage.builder()
                    .imageUrl(imageUrls.get(i))
                    .primary(i == 0)
                    .displayOrder(i)
                    .build());
        }
        images.forEach(product::addImage);
    }

    private Sort resolveSort(String sortBy) {
        if (sortBy == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sortBy) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "rating_desc" -> Sort.by(Sort.Direction.DESC, "rating");
            default -> Sort.by(Sort.Direction.DESC, "createdAt"); // "newest"
        };
    }
}
