package com.example.movieticketbooking.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "discount_code")
public class DiscountCode {
    @Id
    private UUID id;
    @Column(nullable = false, unique = true)
    private String code;
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;
    @Column(name = "max_discount", precision = 12, scale = 2)
    private BigDecimal maxDiscount;
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;
    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;
    @Column(nullable = false)
    private boolean active;

    protected DiscountCode() {
    }

    public DiscountCode(String code, BigDecimal percentage, BigDecimal maxDiscount, LocalDateTime validFrom, LocalDateTime validUntil, boolean active) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.percentage = percentage;
        this.maxDiscount = maxDiscount;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public BigDecimal getMaxDiscount() {
        return maxDiscount;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public boolean isActive() {
        return active;
    }

    public void update(String code, BigDecimal percentage, BigDecimal maxDiscount, LocalDateTime validFrom, LocalDateTime validUntil, boolean active) {
        this.code = code;
        this.percentage = percentage;
        this.maxDiscount = maxDiscount;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.active = active;
    }
}
