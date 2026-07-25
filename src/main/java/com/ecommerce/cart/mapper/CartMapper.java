package com.ecommerce.cart.mapper;

import com.ecommerce.cart.dto.CartDTO;
import com.ecommerce.cart.dto.CartItemDTO;
import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.product.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CartMapper {

    private static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.05); // 5% flat tax, kept simple on purpose

    public CartDTO toDTO(Cart cart) {
        var itemDTOs = cart.getItems().stream().map(this::toItemDTO).toList();

        BigDecimal subtotal = itemDTOs.stream()
                .map(CartItemDTO::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountAmount = cart.getItems().stream()
                .map(this::lineDiscount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxableAmount = subtotal.subtract(discountAmount);
        BigDecimal taxAmount = taxableAmount.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = taxableAmount.add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        return CartDTO.builder()
                .id(cart.getId())
                .items(itemDTOs)
                .subtotal(subtotal.setScale(2, RoundingMode.HALF_UP))
                .discountAmount(discountAmount.setScale(2, RoundingMode.HALF_UP))
                .taxAmount(taxAmount)
                .total(total)
                .itemCount(itemDTOs.size())
                .build();
    }

    private CartItemDTO toItemDTO(CartItem item) {
        return CartItemDTO.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productImageUrl(findPrimaryImageUrl(item))
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .lineTotal(item.getLineTotal())
                .availableStock(item.getProduct().getStock())
                .build();
    }

    /** Applies the product's current discount% against this line's snapshot price */
    private BigDecimal lineDiscount(CartItem item) {
        BigDecimal discountPercent = item.getProduct().getDiscountPercent();
        if (discountPercent == null || discountPercent.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return lineTotal.multiply(discountPercent).divide(BigDecimal.valueOf(100));
    }

    private String findPrimaryImageUrl(CartItem item) {
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
