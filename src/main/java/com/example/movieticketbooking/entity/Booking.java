package com.example.movieticketbooking.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "booking")
public class Booking {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_id")
    private Show show;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount;
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;
    @Column(name = "discount_code")
    private String discountCode;
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    @Column(name = "reminder_sent", nullable = false)
    private boolean reminderSent;

    protected Booking() {
    }

    public Booking(User user, Show show, BigDecimal subtotal, BigDecimal discountAmount, BigDecimal totalAmount, String discountCode, LocalDateTime expiresAt) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.show = show;
        this.status = BookingStatus.PENDING_PAYMENT;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.discountCode = discountCode;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Show getShow() {
        return show;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public void markReminderSent() {
        reminderSent = true;
    }

    public void confirm() {
        status = BookingStatus.CONFIRMED;
        confirmedAt = LocalDateTime.now();
    }

    public void cancel() {
        status = BookingStatus.CANCELLED;
        cancelledAt = LocalDateTime.now();
    }

    public void expire() {
        status = BookingStatus.EXPIRED;
    }
}
