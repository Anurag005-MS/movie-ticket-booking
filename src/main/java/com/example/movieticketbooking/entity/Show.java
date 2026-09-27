package com.example.movieticketbooking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "movie_show")
public class Show {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id")
    private Movie movie;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "theater_id")
    private Theater theater;
    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;
    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    protected Show() {
    }

    public Show(Movie movie, Theater theater, LocalDateTime startsAt, LocalDateTime endsAt) {
        this.id = UUID.randomUUID();
        this.movie = movie;
        this.theater = theater;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public UUID getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public Theater getTheater() {
        return theater;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void update(Movie movie, Theater theater, LocalDateTime startsAt, LocalDateTime endsAt) {
        this.movie = movie;
        this.theater = theater;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }
}
