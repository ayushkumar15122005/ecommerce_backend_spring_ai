package com.ecommerce.cart.controller;

import com.ecommerce.cart.dto.AddToCartRequest;
import com.ecommerce.cart.dto.CartDTO;
import com.ecommerce.cart.dto.UpdateCartItemRequest;
import com.ecommerce.cart.service.CartService;
import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ApiResponse<CartDTO> getCart(@AuthenticationPrincipal UserDetailsImpl principal) {
        return ApiResponse.success(cartService.getCart(principal.getId()));
    }

    @PostMapping("/items")
    public ApiResponse<CartDTO> addItem(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody AddToCartRequest request) {
        return ApiResponse.success("Item added to cart", cartService.addItem(principal.getId(), request));
    }

    @PutMapping("/items/{itemId}")
    public ApiResponse<CartDTO> updateItem(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ApiResponse.success("Cart updated", cartService.updateItemQuantity(principal.getId(), itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<CartDTO> removeItem(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable Long itemId) {
        return ApiResponse.success("Item removed from cart", cartService.removeItem(principal.getId(), itemId));
    }

    @DeleteMapping
    public ApiResponse<Void> clearCart(@AuthenticationPrincipal UserDetailsImpl principal) {
        cartService.clearCart(principal.getId());
        return ApiResponse.success("Cart cleared", null);
    }
}
