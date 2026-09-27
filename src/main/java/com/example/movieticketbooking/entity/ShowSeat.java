package com.example.movieticketbooking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "show_seat", uniqueConstraints = @UniqueConstraint(name = "uq_show_seat", columnNames = {"show_id", "seat_id"}))
public class ShowSeat {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_id")
    private Show show;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id")
    private Seat seat;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShowSeatStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "held_by")
    private User heldBy;
    @Column(name = "held_until")
    private LocalDateTime heldUntil;

    protected ShowSeat() {
    }

    public ShowSeat(Show show, Seat seat) {
        this.id = UUID.randomUUID();
        this.show = show;
        this.seat = seat;
        this.status = ShowSeatStatus.AVAILABLE;
    }

    public UUID getId() {
        return id;
    }

    public Show getShow() {
        return show;
    }

    public Seat getSeat() {
        return seat;
    }

    public ShowSeatStatus getStatus() {
        return status;
    }

    public User getHeldBy() {
        return heldBy;
    }

    public LocalDateTime getHeldUntil() {
        return heldUntil;
    }

    public void hold(User user, LocalDateTime until) {
        status = ShowSeatStatus.HELD;
        heldBy = user;
        heldUntil = until;
    }

    public void book() {
        status = ShowSeatStatus.BOOKED;
        heldBy = null;
        heldUntil = null;
    }

    public void release() {
        status = ShowSeatStatus.AVAILABLE;
        heldBy = null;
        heldUntil = null;
    }
}
