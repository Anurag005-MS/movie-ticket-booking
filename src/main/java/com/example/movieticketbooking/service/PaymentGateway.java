package com.example.movieticketbooking.service;

import java.math.BigDecimal;

public interface PaymentGateway {
    String provider();

    PaymentResult charge(String bookingId, BigDecimal amount);

    PaymentResult refund(String providerReference, BigDecimal amount);

    record PaymentResult(boolean success, String reference, String message) {
    }
}
