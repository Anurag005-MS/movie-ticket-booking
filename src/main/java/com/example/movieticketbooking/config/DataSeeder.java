package com.example.movieticketbooking.config;

import com.example.movieticketbooking.entity.*;
import com.example.movieticketbooking.entity.Role;
import com.example.movieticketbooking.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.*;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seed(UserRepository users, CityRepository cities, TheaterRepository theaters, MovieRepository movies, SeatRepository seats, ShowRepository shows, ShowSeatRepository showSeats, PricingTierRepository pricing, DiscountCodeRepository discounts, RefundPolicyRepository policies, PasswordEncoder encoder) {
        return args -> {
            User admin = users.findByEmailIgnoreCase("admin@example.com").orElseGet(() -> users.save(new User("Admin", "admin@example.com", encoder.encode("Admin@12345"), Role.ADMIN)));
            users.findByEmailIgnoreCase("customer@example.com").orElseGet(() -> users.save(new User("Demo Customer", "customer@example.com", encoder.encode("Customer@12345"), Role.CUSTOMER)));
            City city = cities.findByNameIgnoreCase("Bengaluru").orElseGet(() -> cities.save(new City("Bengaluru")));
            Theater theater = theaters.findByCity_Id(city.getId()).stream().findFirst().orElseGet(() -> theaters.save(new Theater(city, "Demo PVR", "MG Road")));
            if (seats.findByTheater_Id(theater.getId()).isEmpty()) {
                for (int i = 1; i <= 6; i++)
                    seats.save(new Seat(theater, "A", i, i <= 4 ? SeatType.REGULAR : SeatType.PREMIUM));
                for (int i = 1; i <= 6; i++)
                    seats.save(new Seat(theater, "B", i, i <= 4 ? SeatType.REGULAR : SeatType.PREMIUM));
            }
            Movie movie = movies.findAllByOrderByTitleAsc().stream().findFirst().orElseGet(() -> movies.save(new Movie("Demo Movie", "Seeded movie for local development", 140, "English", "Drama", LocalDate.now().minusDays(10))));
            if (shows.findAll().isEmpty()) {
                LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(19).withMinute(0).withSecond(0).withNano(0);
                Show show = shows.save(new Show(movie, theater, start, start.plusMinutes(movie.getDurationMinutes())));
                for (Seat seat : seats.findByTheater_IdOrderByRowLabelAscSeatNumberAsc(theater.getId()))
                    showSeats.save(new ShowSeat(show, seat));
            }
            if (pricing.count() == 0) {
                pricing.save(new PricingTier("REGULAR_WEEKDAY", SeatType.REGULAR, DayType.REGULAR, new BigDecimal("180.00"), true));
                pricing.save(new PricingTier("PREMIUM_WEEKDAY", SeatType.PREMIUM, DayType.REGULAR, new BigDecimal("260.00"), true));
                pricing.save(new PricingTier("REGULAR_WEEKEND", SeatType.REGULAR, DayType.WEEKEND, new BigDecimal("220.00"), true));
                pricing.save(new PricingTier("PREMIUM_WEEKEND", SeatType.PREMIUM, DayType.WEEKEND, new BigDecimal("320.00"), true));
            }
            if (discounts.findByCodeIgnoreCase("WELCOME10").isEmpty())
                discounts.save(new DiscountCode("WELCOME10", new BigDecimal("10"), new BigDecimal("100"), LocalDateTime.now().minusDays(1), LocalDateTime.now().plusMonths(6), true));
            if (policies.count() == 0) {
                policies.save(new RefundPolicy("Full refund 48h+", 48, new BigDecimal("100"), true));
                policies.save(new RefundPolicy("Half refund 24h+", 24, new BigDecimal("50"), true));
                policies.save(new RefundPolicy("No refund <24h", 0, new BigDecimal("0"), true));
            }
        };
    }
}
