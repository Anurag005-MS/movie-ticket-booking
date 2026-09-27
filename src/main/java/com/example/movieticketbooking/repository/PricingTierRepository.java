package com.example.movieticketbooking.repository;

import com.example.movieticketbooking.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface PricingTierRepository extends JpaRepository<PricingTier, UUID> {
    Optional<PricingTier> findBySeatTypeAndDayTypeAndActiveTrue(SeatType seatType, DayType dayType);
}
