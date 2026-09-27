package com.example.movieticketbooking.dto;

import com.example.movieticketbooking.entity.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

public final class ApiDtos {
    private ApiDtos() {
    }

    public record RegisterRequest(@NotBlank String name, @Email @NotBlank String email,
                                  @Size(min = 8, max = 100) String password) {
    }

    public record UserResponse(UUID id, String name, String email, Role role) {
    }

    public record CityRequest(@NotBlank String name) {
    }

    public record CityResponse(UUID id, String name) {
    }

    public record TheaterRequest(@NotNull UUID cityId, @NotBlank String name, String address) {
    }

    public record TheaterResponse(UUID id, UUID cityId, String cityName, String name, String address) {
    }

    public record MovieRequest(@NotBlank String title, String description, @Min(1) int durationMinutes,
                               @NotBlank String language, String genre, LocalDate releaseDate) {
    }

    public record MovieResponse(UUID id, String title, String description, int durationMinutes, String language,
                                String genre, LocalDate releaseDate) {
    }

    public record ShowRequest(@NotNull UUID movieId, @NotNull UUID theaterId, @NotNull LocalDateTime startsAt,
                              @NotNull LocalDateTime endsAt) {
    }

    public record ShowResponse(UUID id, UUID movieId, String movieTitle, UUID theaterId, String theaterName,
                               UUID cityId, String cityName, LocalDateTime startsAt, LocalDateTime endsAt) {
    }

    public record SeatRequest(@NotNull UUID theaterId, @NotBlank String rowLabel, @Min(1) int seatNumber,
                              @NotNull SeatType seatType) {
    }

    public record SeatResponse(UUID id, String rowLabel, int seatNumber, SeatType seatType) {
    }

    public record PricingTierRequest(@NotBlank String name, @NotNull SeatType seatType, @NotNull DayType dayType,
                                     @DecimalMin("0.01") BigDecimal amount, boolean active) {
    }

    public record PricingTierResponse(UUID id, String name, SeatType seatType, DayType dayType, BigDecimal amount,
                                      boolean active) {
    }

    public record DiscountRequest(@NotBlank String code, @DecimalMin("0.01") @DecimalMax("100") BigDecimal percentage,
                                  BigDecimal maxDiscount, @NotNull LocalDateTime validFrom,
                                  @NotNull LocalDateTime validUntil, boolean active) {
    }

    public record DiscountResponse(UUID id, String code, BigDecimal percentage, BigDecimal maxDiscount,
                                   LocalDateTime validFrom, LocalDateTime validUntil, boolean active) {
    }

    public record RefundPolicyRequest(@NotBlank String name, @Min(0) int hoursBeforeShow,
                                      @DecimalMin("0") @DecimalMax("100") BigDecimal refundPercentage, boolean active) {
    }

    public record RefundPolicyResponse(UUID id, String name, int hoursBeforeShow, BigDecimal refundPercentage,
                                       boolean active) {
    }

    public record SeatMapResponse(UUID showSeatId, String rowLabel, int seatNumber, SeatType seatType,
                                  ShowSeatStatus status, LocalDateTime heldUntil) {
    }

    public record HoldRequest(@NotEmpty @Size(max = 10) List<UUID> showSeatIds, String discountCode) {
    }

    public record BookingSeatResponse(UUID showSeatId, String rowLabel, int seatNumber, SeatType seatType,
                                      BigDecimal unitPrice) {
    }

    public record BookingResponse(UUID id, UUID showId, String movieTitle, String theaterName, LocalDateTime startsAt,
                                  BookingStatus status, BigDecimal subtotal, BigDecimal discountAmount,
                                  BigDecimal totalAmount, String discountCode, LocalDateTime expiresAt,
                                  List<BookingSeatResponse> seats) {
    }

    public record PaymentResponse(UUID paymentId, UUID bookingId, String provider, String providerReference,
                                  PaymentStatus status, BigDecimal amount) {
    }

    public record ErrorResponse(String code, String message, LocalDateTime timestamp) {
    }
}
