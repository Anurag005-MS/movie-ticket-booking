package com.example.movieticketbooking.controller;

import com.example.movieticketbooking.dto.ApiDtos.*;
import com.example.movieticketbooking.service.BookingService;
import com.example.movieticketbooking.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {
    private final CatalogService catalog;
    private final BookingService booking;

    public CustomerController(CatalogService catalog, BookingService booking) {
        this.catalog = catalog;
        this.booking = booking;
    }

    @GetMapping("/cities")
    public List<CityResponse> cities() {
        return catalog.cities();
    }

    @GetMapping("/movies")
    public List<MovieResponse> movies() {
        return catalog.movies();
    }

    @GetMapping("/theaters")
    public List<TheaterResponse> theaters(@RequestParam UUID cityId) {
        return catalog.theaters(cityId);
    }

    @GetMapping("/shows")
    public List<ShowResponse> shows(@RequestParam(required = false) UUID cityId, @RequestParam(required = false) UUID movieId, @RequestParam(required = false) UUID theaterId, @RequestParam(required = false) LocalDateTime from, @RequestParam(required = false) LocalDateTime to) {
        return catalog.searchShows(cityId, movieId, theaterId, from, to);
    }

    @GetMapping("/shows/{showId}/seats")
    public List<SeatMapResponse> seatMap(@PathVariable UUID showId) {
        return catalog.seatMap(showId);
    }

    @PostMapping("/shows/{showId}/holds")
    public ResponseEntity<BookingResponse> hold(@PathVariable UUID showId, @Valid @RequestBody HoldRequest r, Authentication a) {
        return ResponseEntity.status(201).body(booking.hold(showId, r, a.getName()));
    }

    @PostMapping("/bookings/{bookingId}/payment/success")
    public PaymentResponse paymentSuccess(@PathVariable UUID bookingId, Authentication a) {
        return booking.pay(bookingId, a.getName());
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public BookingResponse cancel(@PathVariable UUID bookingId, Authentication a) {
        return booking.cancel(bookingId, a.getName());
    }

    @GetMapping("/bookings")
    public List<BookingResponse> history(Authentication a) {
        return booking.history(a.getName());
    }

    @GetMapping("/bookings/{bookingId}")
    public BookingResponse get(@PathVariable UUID bookingId, Authentication a) {
        return booking.get(bookingId, a.getName());
    }
}
