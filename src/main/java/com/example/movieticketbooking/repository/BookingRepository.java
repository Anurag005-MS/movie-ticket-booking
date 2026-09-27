package com.example.movieticketbooking.repository;

import com.example.movieticketbooking.entity.Booking;
import com.example.movieticketbooking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByUser_IdOrderByCreatedAtDesc(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.id = :id")
    Optional<Booking> findByIdForUpdate(@Param("id") UUID id);

    @Query("select b from Booking b where b.status = :status and b.expiresAt < :now")
    List<Booking> findExpired(
            @Param("status") BookingStatus status,
            @Param("now") LocalDateTime now
    );
}
