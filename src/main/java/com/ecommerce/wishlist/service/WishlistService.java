package com.ecommerce.wishlist.service;

import com.ecommerce.common.exception.BadRequestException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.wishlist.dto.WishlistDTO;
import com.ecommerce.wishlist.entity.Wishlist;
import com.ecommerce.wishlist.entity.WishlistItem;
import com.ecommerce.wishlist.mapper.WishlistMapper;
import com.ecommerce.wishlist.repository.WishlistItemRepository;
import com.ecommerce.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final WishlistMapper wishlistMapper;

    @Transactional(readOnly = true)
    public WishlistDTO getWishlist(Long userId) {
        return wishlistMapper.toDTO(getOrCreateWishlist(userId));
    }

    @Transactional
    public WishlistDTO addProduct(Long userId, Long productId) {
        Wishlist wishlist = getOrCreateWishlist(userId);

        if (wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), productId)) {
            throw new BadRequestException("Product is already in your wishlist");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", productId));

        WishlistItem item = WishlistItem.builder()
                .wishlist(wishlist)
                .product(product)
                .build();
        wishlistItemRepository.save(item);

        return wishlistMapper.toDTO(getOrCreateWishlist(userId));
    }

    @Transactional
    public WishlistDTO removeProduct(Long userId, Long itemId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        WishlistItem item = wishlistItemRepository.findByIdAndWishlistId(itemId, wishlist.getId())
                .orElseThrow(() -> ResourceNotFoundException.of("Wishlist item", itemId));

        wishlistItemRepository.delete(item);
        return wishlistMapper.toDTO(getOrCreateWishlist(userId));
    }

    @Transactional
    public Wishlist getOrCreateWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> ResourceNotFoundException.of("User", userId));
            Wishlist wishlist = Wishlist.builder().user(user).build();
            return wishlistRepository.save(wishlist);
        });
    }
}
