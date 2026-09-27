package com.example.movieticketbooking.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "seat")
public class Seat {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "theater_id")
    private Theater theater;
    @Column(name = "row_label", nullable = false)
    private String rowLabel;
    @Column(name = "seat_number", nullable = false)
    private int seatNumber;
    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;

    protected Seat() {
    }

    public Seat(Theater theater, String rowLabel, int seatNumber, SeatType seatType) {
        this.id = UUID.randomUUID();
        this.theater = theater;
        this.rowLabel = rowLabel;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
    }

    public UUID getId() {
        return id;
    }

    public Theater getTheater() {
        return theater;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public void update(String rowLabel, int seatNumber, SeatType seatType) {
        this.rowLabel = rowLabel;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
    }
}
