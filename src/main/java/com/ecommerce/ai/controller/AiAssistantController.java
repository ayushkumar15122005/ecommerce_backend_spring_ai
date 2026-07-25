package com.ecommerce.ai.controller;

import com.ecommerce.ai.dto.ChatRequest;
import com.ecommerce.ai.dto.ChatResponse;
import com.ecommerce.ai.service.AiAssistantService;
import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;

    @PostMapping("/chat")
    public ApiResponse<ChatResponse> chat(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody ChatRequest request) {
        return ApiResponse.success(aiAssistantService.chat(principal.getId(), request));
    }
}
