package com.ecommerce.ai.service;

import org.springframework.stereotype.Component;

@Component
public class FaqService {

    /**
     * Returned as plain text and folded straight into the system prompt in
     * AiAssistantService, rather than exposed as a @Tool - policy text is static,
     * so there's no benefit to a model-initiated tool round trip for it.
     */
    public String getFaqContext() {
        return """
                Shipping policy: Standard shipping takes 3-5 business days and is free on orders over $50;
                orders under $50 have a flat $5.99 shipping fee. Express shipping (1-2 business days) is
                available at checkout for an additional fee.

                Refund policy: Items can be returned within 30 days of delivery for a full refund, provided
                they are unused and in original packaging. Refunds are issued to the original payment method
                within 5-7 business days of the returned item being received.

                Payment methods: We accept major credit/debit cards, UPI, and cash on delivery (COD) for
                eligible orders.

                Warranty: Electronics carry a 1-year manufacturer warranty unless otherwise stated on the
                product page. Fashion, home, and grocery items are not covered by warranty but are eligible
                under the standard refund policy above.
                """;
    }
}
