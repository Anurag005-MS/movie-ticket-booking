package com.example.movieticketbooking.service;

import org.slf4j.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Async
    public void sendConfirmation(UUID bookingId, String email, LocalDateTime showStart) {
        log.info("[ASYNC NOTIFICATION] Booking confirmation: bookingId={}, email={}, showStart={}", bookingId, email, showStart);
    }

    @Async
    public void sendCancellation(UUID bookingId, String email) {
        log.info("[ASYNC NOTIFICATION] Cancellation/refund notification: bookingId={}, email={}", bookingId, email);
    }

    @Async
    public void sendReminder(UUID bookingId, String email, LocalDateTime showStart) {
        log.info("[ASYNC NOTIFICATION] Show reminder: bookingId={}, email={}, showStart={}", bookingId, email, showStart);
    }
}
