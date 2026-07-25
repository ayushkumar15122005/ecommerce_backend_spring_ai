package com.ecommerce.admin.controller;

import com.ecommerce.admin.dto.DashboardStatsDTO;
import com.ecommerce.admin.service.AdminDashboardService;
import com.ecommerce.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    public ApiResponse<DashboardStatsDTO> getStats() {
        return ApiResponse.success(adminDashboardService.getStats());
    }
}
