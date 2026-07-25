package com.ecommerce.order.service;

import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.cart.repository.CartItemRepository;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.common.exception.BadRequestException;
import com.ecommerce.common.exception.InsufficientStockException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.response.PagedResponse;
import com.ecommerce.order.dto.OrderDTO;
import com.ecommerce.order.dto.PlaceOrderRequest;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    // Kept identical to CartMapper's rate so the checkout total matches what the
    // user saw in their cart. In a bigger app this would live in one shared config bean.
    private static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.05);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final OrderStatusValidator orderStatusValidator;

    @Transactional
    public OrderDTO placeOrder(Long userId, PlaceOrderRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Your cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Your cart is empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));

        // Validate stock for every line BEFORE mutating anything
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (cartItem.getQuantity() > product.getStock()) {
                throw new InsufficientStockException(product.getName(), cartItem.getQuantity(), product.getStock());
            }
        }

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            BigDecimal lineTotal = cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            BigDecimal lineDiscount = lineTotal
                    .multiply(product.getDiscountPercent())
                    .divide(BigDecimal.valueOf(100));

            subtotal = subtotal.add(lineTotal);
            discountAmount = discountAmount.add(lineDiscount);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .build();
            order.addItem(orderItem);

            // decrement stock now that we're committed to the order
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        BigDecimal taxableAmount = subtotal.subtract(discountAmount);
        BigDecimal taxAmount = taxableAmount.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = taxableAmount.add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        order.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        order.setDiscountAmount(discountAmount.setScale(2, RoundingMode.HALF_UP));
        order.setTaxAmount(taxAmount);
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);

        // empty the cart now that it has become an order
        cartItemRepository.deleteByCartId(cart.getId());

        return orderMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public PagedResponse<OrderDTO> getOrderHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var result = orderRepository.findByUserId(userId, pageable).map(orderMapper::toDTO);
        return PagedResponse.from(result);
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderDetail(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));
        return orderMapper.toDTO(order);
    }

    @Transactional
    public OrderDTO cancelOwnOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));

        orderStatusValidator.validateTransition(order.getStatus(), OrderStatus.CANCELLED);
        restockItems(order);
        order.setStatus(OrderStatus.CANCELLED);

        return orderMapper.toDTO(orderRepository.save(order));
    }

    // ---- Admin operations ----

    @Transactional(readOnly = true)
    public PagedResponse<OrderDTO> getAllOrders(int page, int size, OrderStatus statusFilter) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var result = (statusFilter != null
                ? orderRepository.findByStatus(statusFilter, pageable)
                : orderRepository.findAllBy(pageable))
                .map(orderMapper::toDTO);
        return PagedResponse.from(result);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));

        orderStatusValidator.validateTransition(order.getStatus(), newStatus);

        if (newStatus == OrderStatus.CANCELLED) {
            restockItems(order);
        }

        order.setStatus(newStatus);
        return orderMapper.toDTO(orderRepository.save(order));
    }

    private void restockItems(Order order) {
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() != null) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }
    }
}
