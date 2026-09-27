package com.example.movieticketbooking.service;

import com.example.movieticketbooking.dto.ApiDtos.*;
import com.example.movieticketbooking.entity.*;
import com.example.movieticketbooking.exception.*;
import com.example.movieticketbooking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CatalogService {
    private final CityRepository cities;
    private final TheaterRepository theaters;
    private final MovieRepository movies;
    private final ShowRepository shows;
    private final SeatRepository seats;
    private final ShowSeatRepository showSeats;
    private final PricingTierRepository pricing;
    private final DiscountCodeRepository discounts;
    private final RefundPolicyRepository policies;

    public CatalogService(CityRepository cities, TheaterRepository theaters, MovieRepository movies, ShowRepository shows, SeatRepository seats, ShowSeatRepository showSeats, PricingTierRepository pricing, DiscountCodeRepository discounts, RefundPolicyRepository policies) {
        this.cities = cities;
        this.theaters = theaters;
        this.movies = movies;
        this.shows = shows;
        this.seats = seats;
        this.showSeats = showSeats;
        this.pricing = pricing;
        this.discounts = discounts;
        this.policies = policies;
    }

    public List<CityResponse> cities() {
        return cities.findAll().stream().map(c -> new CityResponse(c.getId(), c.getName())).toList();
    }

    public CityResponse city(CityRequest r) {
        City c = cities.save(new City(r.name()));
        return new CityResponse(c.getId(), c.getName());
    }

    @Transactional
    public CityResponse updateCity(UUID id, CityRequest r) {
        City c = getCity(id);
        c.setName(r.name());
        return new CityResponse(c.getId(), c.getName());
    }

    public void deleteCity(UUID id) {
        cities.delete(getCity(id));
    }

    @Transactional(readOnly = true)
    public List<TheaterResponse> theaters(UUID cityId) {
        return theaters.findByCity_Id(cityId).stream().map(t -> new TheaterResponse(t.getId(), t.getCity().getId(), t.getCity().getName(), t.getName(), t.getAddress())).toList();
    }

    public TheaterResponse theater(TheaterRequest r) {
        City c = getCity(r.cityId());
        Theater t = theaters.save(new Theater(c, r.name(), r.address()));
        return toTheater(t);
    }

    @Transactional
    public TheaterResponse updateTheater(UUID id, TheaterRequest r) {
        Theater t = getTheater(id);
        if (!t.getCity().getId().equals(r.cityId()))
            throw new BadRequestException("Changing a theater city is not supported; create a new theater instead");
        t.update(r.name(), r.address());
        return toTheater(t);
    }

    public void deleteTheater(UUID id) {
        theaters.delete(getTheater(id));
    }

    public List<MovieResponse> movies() {
        return movies.findAllByOrderByTitleAsc().stream().map(this::toMovie).toList();
    }

    public MovieResponse movie(MovieRequest r) {
        Movie m = movies.save(new Movie(r.title(), r.description(), r.durationMinutes(), r.language(), r.genre(), r.releaseDate()));
        return toMovie(m);
    }

    @Transactional
    public MovieResponse updateMovie(UUID id, MovieRequest r) {
        Movie m = getMovie(id);
        m.update(r.title(), r.description(), r.durationMinutes(), r.language(), r.genre(), r.releaseDate());
        return toMovie(m);
    }

    public void deleteMovie(UUID id) {
        movies.delete(getMovie(id));
    }

    @Transactional
    public ShowResponse show(ShowRequest r) {
        Movie m = getMovie(r.movieId());
        Theater t = getTheater(r.theaterId());
        if (!r.endsAt().isAfter(r.startsAt())) throw new BadRequestException("endsAt must be after startsAt");
        Show s = shows.save(new Show(m, t, r.startsAt(), r.endsAt()));
        seats.findByTheater_Id(t.getId()).forEach(seat -> showSeats.save(new ShowSeat(s, seat)));
        return toShow(s);
    }

    @Transactional
    public ShowResponse updateShow(UUID id, ShowRequest r) {
        Show s = getShow(id);
        Movie m = getMovie(r.movieId());
        Theater t = getTheater(r.theaterId());
        if (!s.getTheater().getId().equals(t.getId()))
            throw new BadRequestException("Changing the theater of an existing show is not supported");
        if (!r.endsAt().isAfter(r.startsAt())) throw new BadRequestException("endsAt must be after startsAt");
        s.update(m, t, r.startsAt(), r.endsAt());
        return toShow(s);
    }

    public void deleteShow(UUID id) {
        shows.delete(getShow(id));
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> searchShows(UUID cityId, UUID movieId, UUID theaterId, LocalDateTime from, LocalDateTime to) {
        LocalDateTime f = from == null ? LocalDateTime.now() : from;
        LocalDateTime tt = to == null ? f.plusDays(7) : to;
        if (theaterId != null)
            return shows.findByTheater_IdAndStartsAtBetweenOrderByStartsAtAsc(theaterId, f, tt).stream().map(this::toShow).toList();
        if (movieId != null)
            return shows.findByMovie_IdAndStartsAtBetweenOrderByStartsAtAsc(movieId, f, tt).stream().filter(s -> cityId == null || s.getTheater().getCity().getId().equals(cityId)).map(this::toShow).toList();
        return shows.findAll().stream().filter(s -> !s.getStartsAt().isBefore(f) && s.getStartsAt().isBefore(tt) && (cityId == null || s.getTheater().getCity().getId().equals(cityId))).sorted(Comparator.comparing(Show::getStartsAt)).map(this::toShow).toList();
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> seats(UUID theaterId) {
        getTheater(theaterId);
        return seats.findByTheater_IdOrderByRowLabelAscSeatNumberAsc(theaterId).stream().map(s -> new SeatResponse(s.getId(), s.getRowLabel(), s.getSeatNumber(), s.getSeatType())).toList();
    }

    public SeatResponse seat(SeatRequest r) {
        Theater t = getTheater(r.theaterId());
        Seat s = seats.save(new Seat(t, r.rowLabel(), r.seatNumber(), r.seatType()));
        return new SeatResponse(s.getId(), s.getRowLabel(), s.getSeatNumber(), s.getSeatType());
    }

    @Transactional
    public SeatResponse updateSeat(UUID id, SeatRequest r) {
        Seat s = seats.findById(id).orElseThrow(() -> new NotFoundException("Seat not found"));
        s.update(r.rowLabel(), r.seatNumber(), r.seatType());
        return new SeatResponse(s.getId(), s.getRowLabel(), s.getSeatNumber(), s.getSeatType());
    }

    public void deleteSeat(UUID id) {
        seats.delete(seats.findById(id).orElseThrow(() -> new NotFoundException("Seat not found")));
    }

    public List<PricingTierResponse> pricing() {
        return pricing.findAll().stream().map(p -> new PricingTierResponse(p.getId(), p.getName(), p.getSeatType(), p.getDayType(), p.getAmount(), p.isActive())).toList();
    }

    public PricingTierResponse price(PricingTierRequest r) {
        PricingTier p = pricing.save(new PricingTier(r.name(), r.seatType(), r.dayType(), r.amount(), r.active()));
        return new PricingTierResponse(p.getId(), p.getName(), p.getSeatType(), p.getDayType(), p.getAmount(), p.isActive());
    }

    @Transactional
    public PricingTierResponse updatePricing(UUID id, PricingTierRequest r) {
        PricingTier p = pricing.findById(id).orElseThrow(() -> new NotFoundException("Pricing tier not found"));
        p.update(r.name(), r.seatType(), r.dayType(), r.amount(), r.active());
        return new PricingTierResponse(p.getId(), p.getName(), p.getSeatType(), p.getDayType(), p.getAmount(), p.isActive());
    }

    public void deletePricing(UUID id) {
        pricing.delete(pricing.findById(id).orElseThrow(() -> new NotFoundException("Pricing tier not found")));
    }

    public List<DiscountResponse> discounts() {
        return discounts.findAll().stream().map(d -> new DiscountResponse(d.getId(), d.getCode(), d.getPercentage(), d.getMaxDiscount(), d.getValidFrom(), d.getValidUntil(), d.isActive())).toList();
    }

    public DiscountResponse discount(DiscountRequest r) {
        if (!r.validUntil().isAfter(r.validFrom())) throw new BadRequestException("validUntil must be after validFrom");
        DiscountCode d = discounts.save(new DiscountCode(r.code().toUpperCase(), r.percentage(), r.maxDiscount(), r.validFrom(), r.validUntil(), r.active()));
        return new DiscountResponse(d.getId(), d.getCode(), d.getPercentage(), d.getMaxDiscount(), d.getValidFrom(), d.getValidUntil(), d.isActive());
    }

    @Transactional
    public DiscountResponse updateDiscount(UUID id, DiscountRequest r) {
        DiscountCode d = discounts.findById(id).orElseThrow(() -> new NotFoundException("Discount code not found"));
        if (!r.validUntil().isAfter(r.validFrom())) throw new BadRequestException("validUntil must be after validFrom");
        d.update(r.code().toUpperCase(), r.percentage(), r.maxDiscount(), r.validFrom(), r.validUntil(), r.active());
        return new DiscountResponse(d.getId(), d.getCode(), d.getPercentage(), d.getMaxDiscount(), d.getValidFrom(), d.getValidUntil(), d.isActive());
    }

    public void deleteDiscount(UUID id) {
        discounts.delete(discounts.findById(id).orElseThrow(() -> new NotFoundException("Discount code not found")));
    }

    public List<RefundPolicyResponse> policies() {
        return policies.findAll().stream().map(p -> new RefundPolicyResponse(p.getId(), p.getName(), p.getHoursBeforeShow(), p.getRefundPercentage(), p.isActive())).toList();
    }

    public RefundPolicyResponse policy(RefundPolicyRequest r) {
        RefundPolicy p = policies.save(new RefundPolicy(r.name(), r.hoursBeforeShow(), r.refundPercentage(), r.active()));
        return new RefundPolicyResponse(p.getId(), p.getName(), p.getHoursBeforeShow(), p.getRefundPercentage(), p.isActive());
    }

    @Transactional
    public RefundPolicyResponse updatePolicy(UUID id, RefundPolicyRequest r) {
        RefundPolicy p = policies.findById(id).orElseThrow(() -> new NotFoundException("Refund policy not found"));
        p.update(r.name(), r.hoursBeforeShow(), r.refundPercentage(), r.active());
        return new RefundPolicyResponse(p.getId(), p.getName(), p.getHoursBeforeShow(), p.getRefundPercentage(), p.isActive());
    }

    public void deletePolicy(UUID id) {
        policies.delete(policies.findById(id).orElseThrow(() -> new NotFoundException("Refund policy not found")));
    }

    public List<SeatMapResponse> seatMap(UUID showId) {
        getShow(showId);
        return showSeats.findByShowIdWithSeat(showId).stream().map(s -> new SeatMapResponse(s.getId(), s.getSeat().getRowLabel(), s.getSeat().getSeatNumber(), s.getSeat().getSeatType(), s.getStatus(), s.getHeldUntil())).toList();
    }

    public City getCity(UUID id) {
        return cities.findById(id).orElseThrow(() -> new NotFoundException("City not found"));
    }

    public Theater getTheater(UUID id) {
        return theaters.findById(id).orElseThrow(() -> new NotFoundException("Theater not found"));
    }

    public Movie getMovie(UUID id) {
        return movies.findById(id).orElseThrow(() -> new NotFoundException("Movie not found"));
    }

    public Show getShow(UUID id) {
        return shows.findById(id).orElseThrow(() -> new NotFoundException("Show not found"));
    }

    private TheaterResponse toTheater(Theater t) {
        return new TheaterResponse(t.getId(), t.getCity().getId(), t.getCity().getName(), t.getName(), t.getAddress());
    }

    private MovieResponse toMovie(Movie m) {
        return new MovieResponse(m.getId(), m.getTitle(), m.getDescription(), m.getDurationMinutes(), m.getLanguage(), m.getGenre(), m.getReleaseDate());
    }

    public ShowResponse toShow(Show s) {
        return new ShowResponse(s.getId(), s.getMovie().getId(), s.getMovie().getTitle(), s.getTheater().getId(), s.getTheater().getName(), s.getTheater().getCity().getId(), s.getTheater().getCity().getName(), s.getStartsAt(), s.getEndsAt());
    }
}
