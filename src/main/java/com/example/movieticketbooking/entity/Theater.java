package com.example.movieticketbooking.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "theater")
public class Theater {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id")
    private City city;
    @Column(nullable = false)
    private String name;
    private String address;

    protected Theater() {
    }

    public Theater(City city, String name, String address) {
        this.id = UUID.randomUUID();
        this.city = city;
        this.name = name;
        this.address = address;
    }

    public UUID getId() {
        return id;
    }

    public City getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void update(String name, String address) {
        this.name = name;
        this.address = address;
    }
}
