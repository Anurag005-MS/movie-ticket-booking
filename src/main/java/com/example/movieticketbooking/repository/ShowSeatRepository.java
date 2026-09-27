package com.example.movieticketbooking.repository;

import com.example.movieticketbooking.entity.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.*;
import java.util.*;

public interface ShowSeatRepository extends JpaRepository<ShowSeat, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ss from ShowSeat ss join fetch ss.seat where ss.show.id = :showId and ss.id = :id")
    Optional<ShowSeat> findForUpdateByShowIdAndId(@Param("showId") UUID showId, @Param("id") UUID id);

    @Query("select ss from ShowSeat ss join fetch ss.seat where ss.show.id = :showId order by ss.seat.rowLabel, ss.seat.seatNumber")
    List<ShowSeat> findByShowIdWithSeat(@Param("showId") UUID showId);

    List<ShowSeat> findByStatusAndHeldUntilBefore(ShowSeatStatus status, LocalDateTime time);
}
