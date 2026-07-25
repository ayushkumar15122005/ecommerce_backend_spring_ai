package com.ecommerce.admin.service;

import com.ecommerce.admin.dto.DashboardStatsDTO;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private static final int LOW_STOCK_THRESHOLD = 10;

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public DashboardStatsDTO getStats() {
        BigDecimal revenue = orderRepository.sumTotalRevenue();

        return DashboardStatsDTO.builder()
                .totalUsers(userRepository.count())
                .totalProducts(productRepository.count())
                .totalOrders(orderRepository.count())
                .pendingOrders(orderRepository.countByStatus(OrderStatus.PENDING))
                .totalRevenue(revenue != null ? revenue : BigDecimal.ZERO)
                .lowStockProductCount(productRepository.countByStockLessThan(LOW_STOCK_THRESHOLD))
                .build();
    }
}
