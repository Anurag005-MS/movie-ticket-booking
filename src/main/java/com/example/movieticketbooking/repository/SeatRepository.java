package com.example.movieticketbooking.repository;

import com.example.movieticketbooking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SeatRepository extends JpaRepository<Seat, UUID> {

    List<Seat> findByTheater_Id(UUID theaterId);

    List<Seat> findByTheater_IdOrderByRowLabelAscSeatNumberAsc(UUID theaterId);
}
