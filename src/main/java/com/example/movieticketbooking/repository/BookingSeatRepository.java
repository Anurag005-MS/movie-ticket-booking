package com.example.movieticketbooking.repository;

import com.example.movieticketbooking.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, UUID> {

    List<BookingSeat> findByBooking_Id(UUID bookingId);
}
