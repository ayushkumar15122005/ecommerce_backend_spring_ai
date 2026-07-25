package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    /**
     * Fetches a product together with its category and images in one query,
     * avoiding lazy-loading exceptions/N+1 when building ProductDetailDTO.
     */
    @EntityGraph(attributePaths = {"category", "images"})
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findWithDetailsById(@Param("id") Long id);

    long countByStockLessThan(int threshold);
}
