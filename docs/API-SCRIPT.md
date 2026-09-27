# API walkthrough / video script

Use this as the script for the endpoint explanation and the 10-minute recorded walkthrough.

## 1. Introduction

“This is a Spring Boot modular monolith for a movie ticket booking system. The core problem is not just CRUD; the important part is safely reserving seats when multiple customers can select the same seat concurrently. I used PostgreSQL persistence, JPA, role-based access control, expiring holds, pricing and discount calculation, a mock payment gateway, refund policies, and asynchronous notifications.”

## 2. Authentication

### POST `/api/v1/auth/register`
Registers a customer. Passwords are BCrypt hashed before persistence. This endpoint is public because a customer needs a way to create an account.

For the demo, two users are seeded:
- admin@example.com / Admin@12345
- customer@example.com / Customer@12345

Authentication for the remaining APIs uses HTTP Basic because advanced authentication is explicitly out of scope.

## 3. Admin/catalog endpoints

### Cities
- `GET /api/v1/admin/cities` — list cities.
- `POST /api/v1/admin/cities` — create a city.
- `DELETE /api/v1/admin/cities/{id}` — remove a city when it has no dependent data.

### Theaters
- `GET /api/v1/admin/theaters?cityId={cityId}` — list theaters in a city.
- `POST /api/v1/admin/theaters` — create a theater under a city.
- `DELETE /api/v1/admin/theaters/{id}` — remove a theater.

### Movies
- `GET /api/v1/admin/movies` — list movies.
- `POST /api/v1/admin/movies` — create a movie.
- `DELETE /api/v1/admin/movies/{id}` — remove a movie.

### Seat layout
- `GET /api/v1/admin/theaters/{theaterId}/seats` — inspect a theater's physical seat layout.
- `POST /api/v1/admin/seats` — create a seat with row, number, and type.
- `DELETE /api/v1/admin/seats/{id}` — remove a seat.

### Shows
- `POST /api/v1/admin/shows` — create a show. When created, the theater's seats are copied into `ShowSeat` records so each show has an independent inventory.
- `DELETE /api/v1/admin/shows/{id}` — remove a show.

### Pricing
- `GET /api/v1/admin/pricing-tiers` — list pricing rules.
- `POST /api/v1/admin/pricing-tiers` — create a pricing rule for seat type + weekday/weekend.
- `DELETE /api/v1/admin/pricing-tiers/{id}` — remove a pricing rule.

### Discounts
- `GET /api/v1/admin/discount-codes` — list discount codes.
- `POST /api/v1/admin/discount-codes` — create a percentage discount with validity dates and optional cap.
- `DELETE /api/v1/admin/discount-codes/{id}` — remove a discount.

### Refund policies
- `GET /api/v1/admin/refund-policies` — list active/inactive refund policies.
- `POST /api/v1/admin/refund-policies` — create a refund policy such as 100% at 48+ hours, 50% at 24+ hours, or 0% below 24 hours.
- `DELETE /api/v1/admin/refund-policies/{id}` — remove a policy.

## 4. Customer browsing

The customer equivalents are:
- `GET /api/v1/cities` — browse cities.
- `GET /api/v1/movies` — browse movies.
- `GET /api/v1/theaters?cityId={cityId}` — browse theaters.
- `GET /api/v1/shows?cityId=&movieId=&theaterId=&from=&to=` — search shows with optional filters.
- `GET /api/v1/shows/{showId}/seats` — show the live seat inventory.

The seat response exposes `AVAILABLE`, `HELD`, or `BOOKED`, which is enough for a frontend to render a seat map later.

## 5. Core booking flow

### POST `/api/v1/shows/{showId}/holds`
Request contains the selected `showSeatIds` and optionally a discount code.

The service starts a database transaction and locks the selected `ShowSeat` rows using a pessimistic write lock. This is the key concurrency decision. If another customer is already holding or booking a selected seat, the request returns HTTP 409 instead of allocating the same seat twice.

The service then:
1. Determines weekday/weekend pricing.
2. Determines regular/premium pricing per seat.
3. Applies the optional discount.
4. Creates a `PENDING_PAYMENT` booking.
5. Marks the seats `HELD` until the configured expiry.
6. Creates a pending payment record.

### POST `/api/v1/bookings/{bookingId}/payment/success`
This is the demo replacement for an external payment provider callback. The `PaymentGateway` abstraction means Stripe/Razorpay/Adyen/etc. can later replace the mock implementation.

The mock gateway returns a successful provider reference. The service then changes the payment to `SUCCESS`, changes each held seat to `BOOKED`, and changes the booking to `CONFIRMED`. Confirmation notification is dispatched asynchronously so it does not block the booking transaction.

## 6. Expiry

A scheduled job runs every configured interval. It finds unpaid bookings whose hold has expired and releases their held seats. The booking is changed to `EXPIRED`.

This prevents abandoned checkout sessions from keeping seats unavailable indefinitely.

## 7. Cancellation and refund

### POST `/api/v1/bookings/{bookingId}/cancel`
Only the booking owner can cancel.

For a confirmed booking, the service checks how many hours remain before the show, selects the applicable refund policy, calls the mock payment gateway for the refund amount, marks the payment `REFUNDED`, releases the seats, and marks the booking `CANCELLED`.

For a pending unpaid booking, no payment refund is needed; the seats are simply released.

## 8. Booking history

- `GET /api/v1/bookings` — current customer's booking history.
- `GET /api/v1/bookings/{bookingId}` — retrieve one owned booking.

The API never allows a customer to read another customer's booking.

## 9. Notifications

Confirmation, cancellation/refund, and show reminders are asynchronous. The current implementation logs notification delivery through `NotificationService`; this is deliberately an adapter point for an email/SMS provider.

A reminder scheduler looks for confirmed shows starting within the next 24 hours and sends each reminder once.

## 10. Error handling

The global exception handler converts validation errors, not-found cases, conflicts, forbidden access, and unexpected errors into a consistent JSON shape containing `code`, `message`, and `timestamp`.

The most important booking error is HTTP 409 for a seat conflict. This makes concurrency behavior explicit to API clients.

## 11. Closing

“The important design decision in this solution is treating show-seat inventory as the concurrency boundary. The database lock serializes competing writes, while the booking and payment state machines keep the flow explicit. The rest of the system is intentionally modular so real payment and notification providers can be substituted without changing the core booking rules.”
