package com.ecommerce.admin.controller;

import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.common.response.PagedResponse;
import com.ecommerce.user.dto.UserProfileDTO;
import com.ecommerce.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<PagedResponse<UserProfileDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(userService.getAllUsers(page, size));
    }

    @PatchMapping("/{id}/enable")
    public ApiResponse<UserProfileDTO> enableUser(@PathVariable Long id) {
        return ApiResponse.success("User enabled", userService.setUserEnabled(id, true));
    }

    @PatchMapping("/{id}/disable")
    public ApiResponse<UserProfileDTO> disableUser(@PathVariable Long id) {
        return ApiResponse.success("User disabled", userService.setUserEnabled(id, false));
    }
}
