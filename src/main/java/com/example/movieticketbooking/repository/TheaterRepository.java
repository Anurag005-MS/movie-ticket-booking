package com.example.movieticketbooking.repository;

import com.example.movieticketbooking.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TheaterRepository extends JpaRepository<Theater, UUID> {

    List<Theater> findByCity_Id(UUID cityId);
}
