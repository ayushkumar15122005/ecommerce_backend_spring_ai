package com.ecommerce.ai.dto;

import com.ecommerce.product.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    private String reply;

    /** Populated when the assistant's answer includes product matches (recommendations, NL search, comparison) */
    private List<ProductDTO> suggestedProducts;

    private String conversationId;
}
