package com.ecommerce.ai.service;

import com.ecommerce.product.dto.ProductDTO;
import com.ecommerce.product.dto.ProductSearchRequest;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.repository.ProductSpecification;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Deliberately NOT a Spring bean/singleton: AiAssistantService creates one instance
 * per chat request so `lastResults` only ever holds this request's matches, which
 * the service then reads back out to populate ChatResponse.suggestedProducts for
 * the frontend's product cards (the plain-text reply alone can't drive UI).
 */
public class ProductRecommendationTool {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final List<ProductDTO> lastResults = new ArrayList<>();

    public ProductRecommendationTool(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Tool(description = "Search the product catalog by keyword, brand, and/or maximum price. " +
            "Use this for product recommendations and natural language search, e.g. 'laptop under $800' " +
            "or 'waterproof shoes under $100'. Leave a parameter empty/null if not mentioned by the user.")
    public List<ProductDTO> searchProducts(String keyword, String brand, Double maxPrice) {
        ProductSearchRequest searchRequest = new ProductSearchRequest();
        searchRequest.setKeyword(keyword);
        searchRequest.setBrand(brand);
        if (maxPrice != null) {
            searchRequest.setMaxPrice(BigDecimal.valueOf(maxPrice));
        }

        var spec = ProductSpecification.fromSearchRequest(searchRequest);
        var page = productRepository.findAll(spec, PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "rating")));

        List<ProductDTO> results = page.map(productMapper::toDTO).getContent();
        lastResults.clear();
        lastResults.addAll(results);
        return results;
    }

    @Tool(description = "Compare two named products side by side (price, rating, brand, stock, discount). " +
            "Provide the two product names as the user wrote them, e.g. 'iPhone 15' and 'Samsung S24'.")
    public String compareProducts(String productName1, String productName2) {
        Product first = findBestMatch(productName1);
        Product second = findBestMatch(productName2);

        if (first == null || second == null) {
            StringBuilder missing = new StringBuilder("I couldn't find: ");
            if (first == null) missing.append(productName1);
            if (first == null && second == null) missing.append(" and ");
            if (second == null) missing.append(productName2);
            return missing.toString();
        }

        lastResults.clear();
        lastResults.add(productMapper.toDTO(first));
        lastResults.add(productMapper.toDTO(second));

        return """
                %s: price $%s (%.0f%% off -> $%s), brand %s, rating %s/5, stock %d
                %s: price $%s (%.0f%% off -> $%s), brand %s, rating %s/5, stock %d
                """.formatted(
                first.getName(), first.getPrice(), first.getDiscountPercent(), first.getFinalPrice(),
                first.getBrand(), first.getRating(), first.getStock(),
                second.getName(), second.getPrice(), second.getDiscountPercent(), second.getFinalPrice(),
                second.getBrand(), second.getRating(), second.getStock()
        );
    }

    public List<ProductDTO> getLastResults() {
        return lastResults;
    }

    private Product findBestMatch(String name) {
        ProductSearchRequest searchRequest = new ProductSearchRequest();
        searchRequest.setKeyword(name);
        var spec = ProductSpecification.fromSearchRequest(searchRequest);
        return productRepository.findAll(spec, PageRequest.of(0, 1)).stream().findFirst().orElse(null);
    }
}
