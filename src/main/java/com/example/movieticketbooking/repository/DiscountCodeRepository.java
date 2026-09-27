package com.example.movieticketbooking.repository;

import com.example.movieticketbooking.entity.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, UUID> {
    Optional<DiscountCode> findByCodeIgnoreCase(String code);
}
