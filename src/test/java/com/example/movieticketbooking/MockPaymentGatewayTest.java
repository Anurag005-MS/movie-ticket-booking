package com.example.movieticketbooking;

import com.example.movieticketbooking.service.MockPaymentGateway;
import com.example.movieticketbooking.service.PaymentGateway;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class MockPaymentGatewayTest {
    private final PaymentGateway gateway = new MockPaymentGateway();

    @Test void chargeReturnsSuccessfulProviderReference() {
        PaymentGateway.PaymentResult result = gateway.charge("booking-1", new BigDecimal("500.00"));
        assertTrue(result.success());
        assertTrue(result.reference().startsWith("MOCK-PAY-"));
    }

    @Test void refundReturnsSuccessfulProviderReference() {
        PaymentGateway.PaymentResult result = gateway.refund("MOCK-PAY-1", new BigDecimal("250.00"));
        assertTrue(result.success());
        assertTrue(result.reference().startsWith("MOCK-REF-"));
    }
}
