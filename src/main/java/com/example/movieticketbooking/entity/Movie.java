package com.example.movieticketbooking.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "movie")
public class Movie {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String title;
    private String description;
    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;
    @Column(nullable = false)
    private String language;
    private String genre;
    @Column(name = "release_date")
    private LocalDate releaseDate;

    protected Movie() {
    }

    public Movie(String title, String description, int durationMinutes, String language, String genre, LocalDate releaseDate) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.language = language;
        this.genre = genre;
        this.releaseDate = releaseDate;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getLanguage() {
        return language;
    }

    public String getGenre() {
        return genre;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void update(String title, String description, int durationMinutes, String language, String genre, LocalDate releaseDate) {
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.language = language;
        this.genre = genre;
        this.releaseDate = releaseDate;
    }
}
