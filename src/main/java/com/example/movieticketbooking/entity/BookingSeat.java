package com.example.movieticketbooking.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "booking_seat")
public class BookingSeat {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id")
    private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_seat_id")
    private ShowSeat showSeat;
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    protected BookingSeat() {
    }

    public BookingSeat(Booking booking, ShowSeat showSeat, BigDecimal unitPrice) {
        this.id = UUID.randomUUID();
        this.booking = booking;
        this.showSeat = showSeat;
        this.unitPrice = unitPrice;
    }

    public UUID getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public ShowSeat getShowSeat() {
        return showSeat;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
