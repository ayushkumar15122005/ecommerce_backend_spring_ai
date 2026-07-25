package com.ecommerce.ai.service;

import com.ecommerce.ai.dto.ChatRequest;
import com.ecommerce.ai.dto.ChatResponse;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiAssistantService {

    private final ChatClient chatClient;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final OrderRepository orderRepository;
    private final FaqService faqService;

    public ChatResponse chat(Long userId, ChatRequest request) {
        // Fresh tool instances per request: ProductRecommendationTool captures this
        // request's matches, OrderStatusTool is hard-scoped to this user's own orders.
        ProductRecommendationTool productTool = new ProductRecommendationTool(productRepository, productMapper);
        OrderStatusTool orderTool = new OrderStatusTool(orderRepository, userId);

        String systemPrompt = buildSystemPrompt();

        String reply = chatClient.prompt()
                .system(systemPrompt)
                .user(request.getMessage())
                .tools(productTool, orderTool)
                .call()
                .content();

        String conversationId = request.getConversationId() != null
                ? request.getConversationId()
                : UUID.randomUUID().toString();

        return ChatResponse.builder()
                .reply(reply)
                .suggestedProducts(productTool.getLastResults())
                .conversationId(conversationId)
                .build();
    }

    private String buildSystemPrompt() {
        return """
                You are the shopping assistant for an e-commerce platform. You help customers with:
                - Product recommendations (e.g. "I need a laptop under $800")
                - Natural language product search (e.g. "show me waterproof shoes under $100")
                - Comparing two named products
                - Answering FAQ questions about shipping, refunds, payment methods, and warranty
                - Explaining the status of the customer's own orders
                - Personalized suggestions based on the customer's past purchases

                Use the available tools to look up real product and order data - never invent product
                names, prices, or order statuses. If a tool returns no results, say so plainly and suggest
                the customer browse the catalog instead.

                For FAQ questions, answer using the following store policy information directly, without
                needing a tool call:

                %s

                Keep responses concise and friendly, formatted as plain conversational text (not JSON).
                """.formatted(faqService.getFaqContext());
    }
}
