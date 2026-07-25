package com.ecommerce.user.controller;

import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.security.UserDetailsImpl;
import com.ecommerce.user.dto.UpdateProfileRequest;
import com.ecommerce.user.dto.UserProfileDTO;
import com.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserProfileDTO> getMyProfile(@AuthenticationPrincipal UserDetailsImpl principal) {
        return ApiResponse.success(userService.getProfile(principal.getId()));
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileDTO> updateMyProfile(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.success("Profile updated successfully", userService.updateProfile(principal.getId(), request));
    }
}
