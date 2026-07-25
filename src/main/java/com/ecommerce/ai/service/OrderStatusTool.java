package com.ecommerce.ai.service;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.repository.OrderRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Deliberately NOT a Spring bean: AiAssistantService instantiates this per request,
 * bound to the authenticated user's id, so the model can only ever query that one
 * user's own orders - never another customer's.
 */
public class OrderStatusTool {

    private final OrderRepository orderRepository;
    private final Long userId;

    public OrderStatusTool(OrderRepository orderRepository, Long userId) {
        this.orderRepository = orderRepository;
        this.userId = userId;
    }

    @Tool(description = "Look up the status of the current user's order by its numeric order id. " +
            "Use this when the user asks about a specific order, e.g. 'where is order 42?'.")
    public String getOrderStatusById(Long orderId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .map(this::describeOrder)
                .orElse("I couldn't find an order with id " + orderId + " on your account.");
    }

    @Tool(description = "Look up the current user's most recent order and its status. " +
            "Use this when the user asks a general question like 'where is my order?' without giving an id.")
    public String getMostRecentOrderStatus() {
        var page = orderRepository.findByUserId(userId, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt")));
        return page.getContent().stream()
                .findFirst()
                .map(this::describeOrder)
                .orElse("You don't have any orders yet.");
    }

    @Tool(description = "Get a short summary of product categories/brands the current user has purchased before. " +
            "Use this to ground personalized recommendations, then call the product search tool with those terms.")
    public String getPastPurchaseSummary() {
        var page = orderRepository.findByUserId(userId, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<String> purchasedNames = page.getContent().stream()
                .flatMap(order -> order.getItems().stream())
                .map(OrderItem::getProductName)
                .distinct()
                .collect(Collectors.toList());

        if (purchasedNames.isEmpty()) {
            return "This user has no past orders to base recommendations on.";
        }
        return "Previously purchased: " + String.join(", ", purchasedNames);
    }

    private String describeOrder(Order order) {
        String items = order.getItems().stream()
                .map(item -> item.getQuantity() + "x " + item.getProductName())
                .collect(Collectors.joining(", "));

        return "Order #%d is currently %s. Items: %s. Total: $%s."
                .formatted(order.getId(), order.getStatus(), items, order.getTotalAmount());
    }
}
