package com.example.movieticketbooking.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingScheduler {
    private final BookingService bookings;

    public BookingScheduler(BookingService bookings) {
        this.bookings = bookings;
    }

    @Scheduled(fixedDelayString = "${booking.scheduler-delay-ms:30000}")
    public void releaseExpiredHolds() {
        bookings.expireDueBookings();
    }

    @Scheduled(fixedDelay = 60000)
    public void reminders() {
        bookings.sendReminders();
    }
}
