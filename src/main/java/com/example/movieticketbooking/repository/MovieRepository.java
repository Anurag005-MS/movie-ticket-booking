package com.example.movieticketbooking.repository;

import com.example.movieticketbooking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface MovieRepository extends JpaRepository<Movie, UUID> {
    List<Movie> findAllByOrderByTitleAsc();
}
