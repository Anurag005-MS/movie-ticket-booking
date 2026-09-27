package com.example.movieticketbooking.repository;

import com.example.movieticketbooking.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ShowRepository extends JpaRepository<Show, UUID> {

    List<Show> findByTheater_IdAndStartsAtBetweenOrderByStartsAtAsc(
            UUID theaterId,
            LocalDateTime from,
            LocalDateTime to
    );

    List<Show> findByMovie_IdAndStartsAtBetweenOrderByStartsAtAsc(
            UUID movieId,
            LocalDateTime from,
            LocalDateTime to
    );
}
