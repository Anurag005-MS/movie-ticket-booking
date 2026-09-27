package com.example.movieticketbooking.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    private UUID id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", unique = true)
    private Booking booking;
    @Column(nullable = false)
    private String provider;
    @Column(name = "provider_reference")
    private String providerReference;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    protected Payment() {
    }

    public Payment(Booking booking, String provider, BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.booking = booking;
        this.provider = provider;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void success(String ref) {
        status = PaymentStatus.SUCCESS;
        providerReference = ref;
        paidAt = LocalDateTime.now();
    }

    public void refund() {
        status = PaymentStatus.REFUNDED;
    }
}
