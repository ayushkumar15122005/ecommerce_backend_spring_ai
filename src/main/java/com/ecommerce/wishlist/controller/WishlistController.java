package com.ecommerce.wishlist.controller;

import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.security.UserDetailsImpl;
import com.ecommerce.wishlist.dto.WishlistDTO;
import com.ecommerce.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ApiResponse<WishlistDTO> getWishlist(@AuthenticationPrincipal UserDetailsImpl principal) {
        return ApiResponse.success(wishlistService.getWishlist(principal.getId()));
    }

    @PostMapping("/{productId}")
    public ApiResponse<WishlistDTO> addProduct(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable Long productId) {
        return ApiResponse.success("Added to wishlist", wishlistService.addProduct(principal.getId(), productId));
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<WishlistDTO> removeItem(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable Long itemId) {
        return ApiResponse.success("Removed from wishlist", wishlistService.removeProduct(principal.getId(), itemId));
    }
}
