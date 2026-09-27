package com.example.movieticketbooking.repository;

import com.example.movieticketbooking.entity.RefundPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface RefundPolicyRepository extends JpaRepository<RefundPolicy, UUID> {
    List<RefundPolicy> findByActiveTrueOrderByHoursBeforeShowDesc();
}
