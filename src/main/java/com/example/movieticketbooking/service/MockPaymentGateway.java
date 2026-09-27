package com.example.movieticketbooking.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MockPaymentGateway implements PaymentGateway {
    public String provider() {
        return "MOCK";
    }

    public PaymentResult charge(String bookingId, BigDecimal amount) {
        return new PaymentResult(true, "MOCK-PAY-" + UUID.randomUUID(), "Payment simulated successfully");
    }

    public PaymentResult refund(String providerReference, BigDecimal amount) {
        return new PaymentResult(true, "MOCK-REF-" + UUID.randomUUID(), "Refund simulated successfully");
    }
}
