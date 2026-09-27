package com.example.movieticketbooking.repository;

import com.example.movieticketbooking.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface CityRepository extends JpaRepository<City, UUID> {
    Optional<City> findByNameIgnoreCase(String name);
}
