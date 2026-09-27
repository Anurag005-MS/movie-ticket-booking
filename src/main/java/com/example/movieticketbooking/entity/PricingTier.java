package com.example.movieticketbooking.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "pricing_tier")
public class PricingTier {
    @Id
    private UUID id;
    @Column(nullable = false, unique = true)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;
    @Enumerated(EnumType.STRING)
    @Column(name = "day_type", nullable = false)
    private DayType dayType;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false)
    private boolean active;

    protected PricingTier() {
    }

    public PricingTier(String name, SeatType seatType, DayType dayType, BigDecimal amount, boolean active) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.seatType = seatType;
        this.dayType = dayType;
        this.amount = amount;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public DayType getDayType() {
        return dayType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean isActive() {
        return active;
    }

    public void update(String name, SeatType seatType, DayType dayType, BigDecimal amount, boolean active) {
        this.name = name;
        this.seatType = seatType;
        this.dayType = dayType;
        this.amount = amount;
        this.active = active;
    }
}
