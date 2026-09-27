package com.example.movieticketbooking.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "city")
public class City {
    @Id
    private UUID id;
    @Column(nullable = false, unique = true)
    private String name;

    protected City() {
    }

    public City(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
