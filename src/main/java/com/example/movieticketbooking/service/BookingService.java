package com.example.movieticketbooking.service;

import com.example.movieticketbooking.dto.ApiDtos.*;
import com.example.movieticketbooking.entity.*;
import com.example.movieticketbooking.exception.*;
import com.example.movieticketbooking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.*;
import java.time.*;
import java.util.*;

@Service
public class BookingService {
    private final UserRepository users;
    private final ShowRepository shows;
    private final ShowSeatRepository showSeats;
    private final BookingRepository bookings;
    private final BookingSeatRepository bookingSeats;
    private final PaymentRepository payments;
    private final PricingTierRepository pricing;
    private final DiscountCodeRepository discounts;
    private final RefundPolicyRepository policies;
    private final PaymentGateway gateway;
    private final NotificationService notifications;
    private final long holdMinutes;

    public BookingService(UserRepository users, ShowRepository shows, ShowSeatRepository showSeats, BookingRepository bookings, BookingSeatRepository bookingSeats, PaymentRepository payments, PricingTierRepository pricing, DiscountCodeRepository discounts, RefundPolicyRepository policies, PaymentGateway gateway, NotificationService notifications, org.springframework.core.env.Environment env) {
        this.users = users;
        this.shows = shows;
        this.showSeats = showSeats;
        this.bookings = bookings;
        this.bookingSeats = bookingSeats;
        this.payments = payments;
        this.pricing = pricing;
        this.discounts = discounts;
        this.policies = policies;
        this.gateway = gateway;
        this.notifications = notifications;
        this.holdMinutes = Long.parseLong(env.getProperty("booking.hold-duration-minutes", "10"));
    }

    @Transactional
    public BookingResponse hold(UUID showId, HoldRequest req, String email) {
        User user = user(email);
        Show show = shows.findById(showId).orElseThrow(() -> new NotFoundException("Show not found"));
        if (show.getStartsAt().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Cannot book a show that has already started");
        List<UUID> ids = req.showSeatIds().stream().distinct().sorted().toList();
        if (ids.size() != req.showSeatIds().size()) throw new BadRequestException("Duplicate seat ids are not allowed");
        List<ShowSeat> selected = new ArrayList<>();
        for (UUID id : ids)
            selected.add(showSeats.findForUpdateByShowIdAndId(showId, id).orElseThrow(() -> new BadRequestException("One or more seats do not belong to this show")));
        LocalDateTime now = LocalDateTime.now();
        selected.forEach(ss -> {
            if (ss.getStatus() == ShowSeatStatus.HELD && ss.getHeldUntil() != null && ss.getHeldUntil().isBefore(now))
                ss.release();
        });
        for (ShowSeat ss : selected)
            if (ss.getStatus() != ShowSeatStatus.AVAILABLE)
                throw new ConflictException("Seat " + ss.getSeat().getRowLabel() + ss.getSeat().getSeatNumber() + " is not available");
        DayType day = (show.getStartsAt().getDayOfWeek() == DayOfWeek.SATURDAY || show.getStartsAt().getDayOfWeek() == DayOfWeek.SUNDAY) ? DayType.WEEKEND : DayType.REGULAR;
        Map<UUID, BigDecimal> prices = new HashMap<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ShowSeat ss : selected) {
            PricingTier tier = pricing.findBySeatTypeAndDayTypeAndActiveTrue(ss.getSeat().getSeatType(), day).orElseThrow(() -> new BadRequestException("No active pricing for " + ss.getSeat().getSeatType() + " on " + day));
            prices.put(ss.getId(), tier.getAmount());
            subtotal = subtotal.add(tier.getAmount());
        }
        BigDecimal discount = calculateDiscount(req.discountCode(), subtotal);
        BigDecimal total = subtotal.subtract(discount).max(BigDecimal.ZERO);
        LocalDateTime expiry = now.plusMinutes(holdMinutes);
        Booking b = bookings.save(new Booking(user, show, subtotal, discount, total, req.discountCode() == null ? null : req.discountCode().toUpperCase(), expiry));
        for (ShowSeat ss : selected) {
            ss.hold(user, expiry);
            bookingSeats.save(new BookingSeat(b, ss, prices.get(ss.getId())));
        }
        payments.save(new Payment(b, gateway.provider(), total));
        return toResponse(b, bookingSeats.findByBooking_Id(b.getId()));
    }

    @Transactional
    public PaymentResponse pay(UUID bookingId, String email) {
        Booking b = getBooking(bookingId);
        checkOwner(b, email);
        if (b.getStatus() != BookingStatus.PENDING_PAYMENT)
            throw new ConflictException("Booking is not awaiting payment");
        if (b.getExpiresAt() == null || b.getExpiresAt().isBefore(LocalDateTime.now())) {
            expireOne(b);
            throw new ConflictException("Booking hold has expired");
        }
        Payment p = payments.findByBooking_Id(bookingId).orElseThrow(() -> new NotFoundException("Payment not found"));
        if (p.getStatus() == PaymentStatus.SUCCESS) return toPayment(p);
        PaymentGateway.PaymentResult result = gateway.charge(bookingId.toString(), b.getTotalAmount());
        if (!result.success()) {
            throw new ConflictException("Payment failed: " + result.message());
        }
        p.success(result.reference());
        bookingSeats.findByBooking_Id(bookingId).forEach(bs -> bs.getShowSeat().book());
        b.confirm();
        notifications.sendConfirmation(b.getId(), b.getUser().getEmail(), b.getShow().getStartsAt());
        return toPayment(p);
    }

    @Transactional
    public BookingResponse cancel(UUID bookingId, String email) {
        Booking b = getBooking(bookingId);
        checkOwner(b, email);
        if (b.getStatus() == BookingStatus.CANCELLED || b.getStatus() == BookingStatus.EXPIRED)
            throw new ConflictException("Booking is already closed");
        if (b.getStatus() == BookingStatus.PENDING_PAYMENT) {
            bookingSeats.findByBooking_Id(bookingId).forEach(bs -> bs.getShowSeat().release());
            b.cancel();
            return toResponse(b, bookingSeats.findByBooking_Id(bookingId));
        }
        if (b.getShow().getStartsAt().isBefore(LocalDateTime.now()))
            throw new ConflictException("Cannot cancel after the show has started");
        long hours = Duration.between(LocalDateTime.now(), b.getShow().getStartsAt()).toHours();
        RefundPolicy policy = policies.findByActiveTrueOrderByHoursBeforeShowDesc().stream().filter(p -> hours >= p.getHoursBeforeShow()).findFirst().orElse(null);
        if (policy == null) throw new BadRequestException("No applicable refund policy");
        Payment p = payments.findByBooking_Id(bookingId).orElseThrow(() -> new NotFoundException("Payment not found"));
        BigDecimal refund = b.getTotalAmount().multiply(policy.getRefundPercentage()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        if (refund.signum() > 0) {
            PaymentGateway.PaymentResult r = gateway.refund(p.getProviderReference(), refund);
            if (!r.success()) throw new ConflictException("Refund failed");
            p.refund();
        }
        bookingSeats.findByBooking_Id(bookingId).forEach(bs -> bs.getShowSeat().release());
        b.cancel();
        notifications.sendCancellation(b.getId(), b.getUser().getEmail());
        return toResponse(b, bookingSeats.findByBooking_Id(bookingId));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> history(String email) {
        User u = user(email);
        return bookings.findByUser_IdOrderByCreatedAtDesc(u.getId()).stream().map(b -> toResponse(b, bookingSeats.findByBooking_Id(b.getId()))).toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse get(UUID id, String email) {
        Booking b = getBooking(id);
        checkOwner(b, email);
        return toResponse(b, bookingSeats.findByBooking_Id(id));
    }

    @Transactional
    public void expireDueBookings() {
        LocalDateTime now = LocalDateTime.now();
        for (Booking b : bookings.findExpired(BookingStatus.PENDING_PAYMENT, now)) {
            Booking locked = bookings.findByIdForUpdate(b.getId()).orElse(null);
            if (locked != null && locked.getStatus() == BookingStatus.PENDING_PAYMENT && locked.getExpiresAt() != null && locked.getExpiresAt().isBefore(now))
                expireOne(locked);
        }
    }

    @Transactional
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now(), until = now.plusHours(24);
        for (Booking b : bookings.findAll()) {
            if (b.getStatus() == BookingStatus.CONFIRMED && !b.isReminderSent() && b.getShow().getStartsAt().isAfter(now) && b.getShow().getStartsAt().isBefore(until)) {
                b.markReminderSent();
                notifications.sendReminder(b.getId(), b.getUser().getEmail(), b.getShow().getStartsAt());
            }
        }
    }

    private void expireOne(Booking b) {
        bookingSeats.findByBooking_Id(b.getId()).forEach(bs -> {
            ShowSeat ss = bs.getShowSeat();
            if (ss.getStatus() == ShowSeatStatus.HELD && ss.getHeldUntil() != null && ss.getHeldUntil().isBefore(LocalDateTime.now()))
                ss.release();
        });
        b.expire();
    }

    private BigDecimal calculateDiscount(String code, BigDecimal subtotal) {
        if (code == null || code.isBlank()) return BigDecimal.ZERO;
        DiscountCode d = discounts.findByCodeIgnoreCase(code).orElseThrow(() -> new BadRequestException("Invalid discount code"));
        LocalDateTime now = LocalDateTime.now();
        if (!d.isActive() || now.isBefore(d.getValidFrom()) || now.isAfter(d.getValidUntil()))
            throw new BadRequestException("Discount code is not active or valid");
        BigDecimal x = subtotal.multiply(d.getPercentage()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return d.getMaxDiscount() == null ? x : x.min(d.getMaxDiscount());
    }

    private User user(String email) {
        return users.findByEmailIgnoreCase(email).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Booking getBooking(UUID id) {
        return bookings.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    private void checkOwner(Booking b, String email) {
        if (!b.getUser().getEmail().equalsIgnoreCase(email))
            throw new ForbiddenException("You do not own this booking");
    }

    private BookingResponse toResponse(Booking b, List<BookingSeat> ss) {
        return new BookingResponse(b.getId(), b.getShow().getId(), b.getShow().getMovie().getTitle(), b.getShow().getTheater().getName(), b.getShow().getStartsAt(), b.getStatus(), b.getSubtotal(), b.getDiscountAmount(), b.getTotalAmount(), b.getDiscountCode(), b.getExpiresAt(), ss.stream().map(x -> new BookingSeatResponse(x.getShowSeat().getId(), x.getShowSeat().getSeat().getRowLabel(), x.getShowSeat().getSeat().getSeatNumber(), x.getShowSeat().getSeat().getSeatType(), x.getUnitPrice())).toList());
    }

    private PaymentResponse toPayment(Payment p) {
        return new PaymentResponse(p.getId(), p.getBooking().getId(), p.getProvider(), p.getProviderReference(), p.getStatus(), p.getAmount());
    }
}
