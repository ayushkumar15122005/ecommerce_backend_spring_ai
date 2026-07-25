package com.ecommerce.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequest {

    @NotBlank(message = "Message is required")
    private String message;

    /** Optional: lets the client maintain a running conversation id/history if desired */
    private String conversationId;
}
