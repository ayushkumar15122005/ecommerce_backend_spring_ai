package com.ecommerce.wishlist.repository;

import com.ecommerce.wishlist.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    Optional<WishlistItem> findByWishlistIdAndProductId(Long wishlistId, Long productId);

    Optional<WishlistItem> findByIdAndWishlistId(Long id, Long wishlistId);

    boolean existsByWishlistIdAndProductId(Long wishlistId, Long productId);
}
