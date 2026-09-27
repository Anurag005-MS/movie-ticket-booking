# Movie Ticket Booking System

SDE-2 take-home implementation for the supplied Movie Ticket Booking System specification. The assignment explicitly asks for a Spring Boot REST API, database persistence, role-based access control, validation/error handling, and unit/integration tests. It also calls out seat-level booking with expiring holds, pricing tiers, discounts, payments, refunds, asynchronous notifications, and safe serialization of concurrent seat booking attempts.

## Source interpretation

The supplied assignment is intentionally open-ended and asks the candidate to own the scoping decisions. This implementation therefore chooses a modular monolith with Spring Boot, PostgreSQL, JPA, Flyway, HTTP Basic authentication, and a mock payment gateway. UI, deployment, containers, CI/CD, microservices, advanced authentication, and production observability are intentionally excluded, matching the assignment's out-of-scope guidance.

## Architecture

- Spring Boot 3.x / Java 17
- Spring MVC + Bean Validation
- Spring Data JPA / Hibernate
- PostgreSQL + Flyway migrations
- H2 for tests
- Spring Security HTTP Basic + BCrypt
- Scheduled jobs for hold expiry and reminders
- `PaymentGateway` interface + `MockPaymentGateway` implementation so a real provider can later be plugged in without changing booking logic
- Async notification service using `@Async`
- Pessimistic row locking on `ShowSeat` during hold creation to serialize competing seat requests

### Main domain model

`User -> Booking -> BookingSeat -> ShowSeat -> Seat -> Theater -> City`

`Show -> Movie + Theater`

`PricingTier` determines price from seat type + weekday/weekend.
`DiscountCode` applies a percentage discount with an optional cap.
`RefundPolicy` determines cancellation refund percentage based on hours before show.
`Payment` records the payment state and provider reference.

## Booking flow

1. Customer authenticates using HTTP Basic.
2. Customer browses shows and reads the show seat map.
3. Customer posts selected `showSeatIds` to `/shows/{showId}/holds`.
4. The service locks those rows with `PESSIMISTIC_WRITE`.
5. If any selected seat is already held/booked, the whole hold request fails with HTTP 409; no double allocation occurs.
6. A `Booking` in `PENDING_PAYMENT` is created and each seat becomes `HELD` until the configured expiry.
7. A pending payment record is created.
8. Customer calls `/bookings/{bookingId}/payment/success`. This invokes the mock gateway, marks payment successful, converts seats to `BOOKED`, and confirms the booking.
9. Confirmation notification is dispatched asynchronously.
10. A scheduled job expires unpaid bookings and releases their seats.
11. A scheduled job sends a reminder for confirmed shows within 24 hours.
12. Cancellation calculates the configured refund policy and invokes the mock gateway refund before releasing seats.

## Concurrency decision

The critical invariant is that a `ShowSeat` can only transition from `AVAILABLE` to `HELD` when the row is locked. The repository query used by the hold flow applies `PESSIMISTIC_WRITE`, so concurrent transactions attempting the same show-seat rows are serialized by the database. A unique `(show_id, seat_id)` constraint additionally prevents duplicate show-seat records.

## Payment decision

A real payment provider would be external and should be integrated behind `PaymentGateway`. For the take-home, `MockPaymentGateway` always returns a successful payment/refund reference. The endpoint is deliberately named `payment/success` so the end-to-end flow can be demonstrated without a merchant account or external secrets.

## Authentication / roles

- `ADMIN`: can manage cities, theaters, movies, shows, seats, pricing, discount codes, and refund policies.
- `CUSTOMER`: can browse, hold, pay, cancel, and view own bookings.
- Advanced OAuth/SSO/MFA is intentionally not included because it is explicitly out of scope.

### Seeded development users

- Admin: `admin@example.com` / `Admin@12345`
- Customer: `customer@example.com` / `Customer@12345`

Change these before using the project beyond local evaluation.

## Configuration

Environment variables:

- `DB_URL` (default `jdbc:postgresql://localhost:5432/movie_booking`)
- `DB_USERNAME` (default `postgres`)
- `DB_PASSWORD` (default `postgres`)
- `SERVER_PORT` (default `8080`)
- `HOLD_DURATION_MINUTES` (default `10`)
- `HOLD_SCHEDULER_DELAY_MS` (default `30000`)

## Run locally

1. Create PostgreSQL database `movie_booking`.
2. Set database environment variables if required.
3. Run `./gradlew bootRun`.
4. Flyway creates the schema and the seed runner creates demo data.
5. Import `docs/movie-ticket-booking.postman_collection.json` into Postman, or follow `docs/API-SCRIPT.md`.

## Tests

The test suite is intended to cover registration, booking/concurrency-critical behavior, payment confirmation, cancellation/refund behavior, and security boundaries. The project is configured to use H2 for the test profile.

## Meaningful assumptions

- One show belongs to one movie and one theater.
- A theater's seat layout is copied to each new show as `ShowSeat` records.
- Seat types are `REGULAR` and `PREMIUM`.
- Pricing is selected by seat type and whether the show starts on Saturday/Sunday.
- Discount codes are percentage based and may have a maximum discount cap.
- Holds last `10` minutes by default and can be configured through environment variables.
- Payment is required before a hold becomes a confirmed booking.
- A pending unpaid booking is expired automatically after its hold duration.
- Cancellation after confirmation uses the first active refund policy whose `hoursBeforeShow` threshold is satisfied.
- Notification delivery is represented by asynchronous log-based notification handling; a real email/SMS provider can be plugged into `NotificationService`.
- All date/time values are represented as local server/application time for this take-home. Production deployments should standardize on UTC and explicitly convert at API boundaries.
