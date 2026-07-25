package com.ecommerce.product.entity;

import com.ecommerce.category.entity.Category;
import com.ecommerce.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "discount_percent", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discountPercent = BigDecimal.ZERO;

    @Column(length = 100)
    private String brand;

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Column(nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(length = 255)
    private String tags; // comma-separated, e.g. "wireless,bluetooth,noise-cancelling"

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    // Convenience method to keep both sides of the relationship in sync
    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    /** Computed helper, not persisted */
    @Transient
    public BigDecimal getFinalPrice() {
        BigDecimal discount = price.multiply(discountPercent).divide(BigDecimal.valueOf(100));
        return price.subtract(discount);
    }
}
