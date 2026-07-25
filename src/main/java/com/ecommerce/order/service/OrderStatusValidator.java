package com.ecommerce.order.service;

import com.ecommerce.common.exception.BadRequestException;
import com.ecommerce.order.entity.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Single source of truth for legal order status transitions:
 *
 *   PENDING -> CONFIRMED -> SHIPPED -> DELIVERED
 *   PENDING -> CANCELLED
 *   CONFIRMED -> CANCELLED
 *
 * SHIPPED, DELIVERED and CANCELLED are terminal-ish (SHIPPED can still be cancelled
 * in some businesses, but we keep this simple/strict for a portfolio project).
 */
@Component
public class OrderStatusValidator {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(OrderStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(OrderStatus.PENDING, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.SHIPPED, EnumSet.of(OrderStatus.DELIVERED));
        ALLOWED_TRANSITIONS.put(OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class));
        ALLOWED_TRANSITIONS.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
    }

    public void validateTransition(OrderStatus current, OrderStatus target) {
        if (current == target) {
            throw new BadRequestException("Order is already in status " + current);
        }
        Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(target)) {
            throw new BadRequestException(
                    "Cannot transition order from " + current + " to " + target);
        }
    }
}
