package com.ecommerce.cart.service;

import com.ecommerce.cart.dto.AddToCartRequest;
import com.ecommerce.cart.dto.CartDTO;
import com.ecommerce.cart.dto.UpdateCartItemRequest;
import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.cart.mapper.CartMapper;
import com.ecommerce.cart.repository.CartItemRepository;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.common.exception.InsufficientStockException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Transactional(readOnly = true)
    public CartDTO getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cartMapper.toDTO(cart);
    }

    @Transactional
    public CartDTO addItem(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> ResourceNotFoundException.of("Product", request.getProductId()));

        CartItem existing = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);
        int desiredQuantity = (existing != null ? existing.getQuantity() : 0) + request.getQuantity();

        if (desiredQuantity > product.getStock()) {
            throw new InsufficientStockException(product.getName(), desiredQuantity, product.getStock());
        }

        if (existing != null) {
            existing.setQuantity(desiredQuantity);
            cartItemRepository.save(existing);
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .unitPrice(product.getFinalPrice())
                    .build();
            cartItemRepository.save(item);
        }

        return cartMapper.toDTO(getOrCreateCart(userId));
    }

    @Transactional
    public CartDTO updateItemQuantity(Long userId, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> ResourceNotFoundException.of("Cart item", itemId));

        if (request.getQuantity() > item.getProduct().getStock()) {
            throw new InsufficientStockException(item.getProduct().getName(), request.getQuantity(), item.getProduct().getStock());
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return cartMapper.toDTO(getOrCreateCart(userId));
    }

    @Transactional
    public CartDTO removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> ResourceNotFoundException.of("Cart item", itemId));

        cartItemRepository.delete(item);
        return cartMapper.toDTO(getOrCreateCart(userId));
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    /**
     * Every user gets exactly one cart, created lazily on first access
     * (rather than at registration time) to keep AuthService simple.
     */
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> ResourceNotFoundException.of("User", userId));
            Cart cart = Cart.builder().user(user).build();
            return cartRepository.save(cart);
        });
    }
}
