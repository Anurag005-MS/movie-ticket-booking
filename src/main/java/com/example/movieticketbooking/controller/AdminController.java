package com.example.movieticketbooking.controller;

import com.example.movieticketbooking.dto.ApiDtos.*;
import com.example.movieticketbooking.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final CatalogService s;

    public AdminController(CatalogService s) {
        this.s = s;
    }

    @GetMapping("/cities")
    public List<CityResponse> cities() {
        return s.cities();
    }

    @PostMapping("/cities")
    public ResponseEntity<CityResponse> city(@Valid @RequestBody CityRequest r) {
        return ResponseEntity.status(201).body(s.city(r));
    }

    @PutMapping("/cities/{id}")
    public CityResponse updateCity(@PathVariable UUID id, @Valid @RequestBody CityRequest r) {
        return s.updateCity(id, r);
    }

    @DeleteMapping("/cities/{id}")
    public void deleteCity(@PathVariable UUID id) {
        s.deleteCity(id);
    }

    @GetMapping("/theaters")
    public List<TheaterResponse> theaters(@RequestParam UUID cityId) {
        return s.theaters(cityId);
    }

    @PostMapping("/theaters")
    public ResponseEntity<TheaterResponse> theater(@Valid @RequestBody TheaterRequest r) {
        return ResponseEntity.status(201).body(s.theater(r));
    }

    @PutMapping("/theaters/{id}")
    public TheaterResponse updateTheater(@PathVariable UUID id, @Valid @RequestBody TheaterRequest r) {
        return s.updateTheater(id, r);
    }

    @DeleteMapping("/theaters/{id}")
    public void deleteTheater(@PathVariable UUID id) {
        s.deleteTheater(id);
    }

    @GetMapping("/movies")
    public List<MovieResponse> movies() {
        return s.movies();
    }

    @PostMapping("/movies")
    public ResponseEntity<MovieResponse> movie(@Valid @RequestBody MovieRequest r) {
        return ResponseEntity.status(201).body(s.movie(r));
    }

    @PutMapping("/movies/{id}")
    public MovieResponse updateMovie(@PathVariable UUID id, @Valid @RequestBody MovieRequest r) {
        return s.updateMovie(id, r);
    }

    @DeleteMapping("/movies/{id}")
    public void deleteMovie(@PathVariable UUID id) {
        s.deleteMovie(id);
    }

    @PostMapping("/shows")
    public ResponseEntity<ShowResponse> show(@Valid @RequestBody ShowRequest r) {
        return ResponseEntity.status(201).body(s.show(r));
    }

    @PutMapping("/shows/{id}")
    public ShowResponse updateShow(@PathVariable UUID id, @Valid @RequestBody ShowRequest r) {
        return s.updateShow(id, r);
    }

    @DeleteMapping("/shows/{id}")
    public void deleteShow(@PathVariable UUID id) {
        s.deleteShow(id);
    }

    @GetMapping("/theaters/{theaterId}/seats")
    public List<SeatResponse> seats(@PathVariable UUID theaterId) {
        return s.seats(theaterId);
    }

    @PostMapping("/seats")
    public ResponseEntity<SeatResponse> seat(@Valid @RequestBody SeatRequest r) {
        return ResponseEntity.status(201).body(s.seat(r));
    }

    @PutMapping("/seats/{id}")
    public SeatResponse updateSeat(@PathVariable UUID id, @Valid @RequestBody SeatRequest r) {
        return s.updateSeat(id, r);
    }

    @DeleteMapping("/seats/{id}")
    public void deleteSeat(@PathVariable UUID id) {
        s.deleteSeat(id);
    }

    @GetMapping("/pricing-tiers")
    public List<PricingTierResponse> pricing() {
        return s.pricing();
    }

    @PostMapping("/pricing-tiers")
    public ResponseEntity<PricingTierResponse> price(@Valid @RequestBody PricingTierRequest r) {
        return ResponseEntity.status(201).body(s.price(r));
    }

    @PutMapping("/pricing-tiers/{id}")
    public PricingTierResponse updatePricing(@PathVariable UUID id, @Valid @RequestBody PricingTierRequest r) {
        return s.updatePricing(id, r);
    }

    @DeleteMapping("/pricing-tiers/{id}")
    public void deletePricing(@PathVariable UUID id) {
        s.deletePricing(id);
    }

    @GetMapping("/discount-codes")
    public List<DiscountResponse> discounts() {
        return s.discounts();
    }

    @PostMapping("/discount-codes")
    public ResponseEntity<DiscountResponse> discount(@Valid @RequestBody DiscountRequest r) {
        return ResponseEntity.status(201).body(s.discount(r));
    }

    @PutMapping("/discount-codes/{id}")
    public DiscountResponse updateDiscount(@PathVariable UUID id, @Valid @RequestBody DiscountRequest r) {
        return s.updateDiscount(id, r);
    }

    @DeleteMapping("/discount-codes/{id}")
    public void deleteDiscount(@PathVariable UUID id) {
        s.deleteDiscount(id);
    }

    @GetMapping("/refund-policies")
    public List<RefundPolicyResponse> policies() {
        return s.policies();
    }

    @PostMapping("/refund-policies")
    public ResponseEntity<RefundPolicyResponse> policy(@Valid @RequestBody RefundPolicyRequest r) {
        return ResponseEntity.status(201).body(s.policy(r));
    }

    @PutMapping("/refund-policies/{id}")
    public RefundPolicyResponse updatePolicy(@PathVariable UUID id, @Valid @RequestBody RefundPolicyRequest r) {
        return s.updatePolicy(id, r);
    }

    @DeleteMapping("/refund-policies/{id}")
    public void deletePolicy(@PathVariable UUID id) {
        s.deletePolicy(id);
    }
}
