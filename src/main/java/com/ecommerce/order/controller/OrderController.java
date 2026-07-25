package com.ecommerce.order.controller;

import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.common.response.PagedResponse;
import com.ecommerce.order.dto.OrderDTO;
import com.ecommerce.order.dto.PlaceOrderRequest;
import com.ecommerce.order.dto.UpdateOrderStatusRequest;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.service.OrderService;
import com.ecommerce.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ---- Customer endpoints ----

    @PostMapping
    public ApiResponse<OrderDTO> placeOrder(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody PlaceOrderRequest request) {
        return ApiResponse.success("Order placed successfully", orderService.placeOrder(principal.getId(), request));
    }

    @GetMapping
    public ApiResponse<PagedResponse<OrderDTO>> getMyOrders(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(orderService.getOrderHistory(principal.getId(), page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDTO> getMyOrder(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable Long id) {
        return ApiResponse.success(orderService.getOrderDetail(principal.getId(), id));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderDTO> cancelMyOrder(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable Long id) {
        return ApiResponse.success("Order cancelled", orderService.cancelOwnOrder(principal.getId(), id));
    }

    // ---- Admin endpoints ----

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PagedResponse<OrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status) {
        return ApiResponse.success(orderService.getAllOrders(page, size, status));
    }

    @PatchMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ApiResponse.success("Order status updated", orderService.updateOrderStatus(id, request.getStatus()));
    }
}
