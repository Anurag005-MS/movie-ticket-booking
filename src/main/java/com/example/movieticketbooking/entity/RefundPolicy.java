package com.example.movieticketbooking.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "refund_policy")
public class RefundPolicy {
    @Id
    private UUID id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(name = "hours_before_show", nullable = false)
    private int hoursBeforeShow;
    @Column(name = "refund_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal refundPercentage;
    @Column(nullable = false)
    private boolean active;

    protected RefundPolicy() {
    }

    public RefundPolicy(String name, int hoursBeforeShow, BigDecimal refundPercentage, boolean active) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.hoursBeforeShow = hoursBeforeShow;
        this.refundPercentage = refundPercentage;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHoursBeforeShow() {
        return hoursBeforeShow;
    }

    public BigDecimal getRefundPercentage() {
        return refundPercentage;
    }

    public boolean isActive() {
        return active;
    }

    public void update(String name, int hoursBeforeShow, BigDecimal refundPercentage, boolean active) {
        this.name = name;
        this.hoursBeforeShow = hoursBeforeShow;
        this.refundPercentage = refundPercentage;
        this.active = active;
    }
}
