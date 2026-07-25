package com.ecommerce.wishlist.mapper;

import com.ecommerce.product.entity.ProductImage;
import com.ecommerce.wishlist.dto.WishlistDTO;
import com.ecommerce.wishlist.dto.WishlistItemDTO;
import com.ecommerce.wishlist.entity.Wishlist;
import com.ecommerce.wishlist.entity.WishlistItem;
import org.springframework.stereotype.Component;

@Component
public class WishlistMapper {

    public WishlistDTO toDTO(Wishlist wishlist) {
        return WishlistDTO.builder()
                .id(wishlist.getId())
                .items(wishlist.getItems().stream().map(this::toItemDTO).toList())
                .build();
    }

    private WishlistItemDTO toItemDTO(WishlistItem item) {
        var product = item.getProduct();
        return WishlistItemDTO.builder()
                .id(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImageUrl(findPrimaryImageUrl(item))
                .price(product.getPrice())
                .finalPrice(product.getFinalPrice())
                .inStock(product.getStock() != null && product.getStock() > 0)
                .addedAt(item.getAddedAt())
                .build();
    }

    private String findPrimaryImageUrl(WishlistItem item) {
        var images = item.getProduct().getImages();
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.stream()
                .filter(ProductImage::isPrimary)
                .findFirst()
                .orElse(images.get(0))
                .getImageUrl();
    }
}
