# Agents.md

## Development brief

Build the Movie Ticket Booking System from the supplied SDE-2 take-home specification.

## Constraints

- Spring Boot / Java 17.
- REST API only; no frontend.
- Persist data in PostgreSQL; use H2 for tests.
- Basic admin/customer role-based access control.
- Seat holds must expire automatically.
- Concurrent attempts to book the same seat must not double allocate.
- Include pricing tiers, discounts, payment, confirmation, cancellation/refund, and asynchronous notifications.
- Keep deployment/containerization/microservices/advanced authentication/production observability out of scope.

## Engineering choices

1. Modular monolith to keep the transactional booking boundary in one process.
2. JPA pessimistic row locking on `ShowSeat` for the critical inventory operation.
3. Explicit booking/payment state enums.
4. `PaymentGateway` abstraction with a mock provider for the take-home.
5. Scheduled expiry/reminder jobs.
6. Async notification adapter.
7. Flyway for deterministic database schema management.
8. DTOs + validation + centralized exception mapping.

## Verification checklist

- Compile and run tests before submission.
- Verify admin/customer authorization.
- Verify duplicate seat requests return 409.
- Verify payment moves seats to BOOKED.
- Verify unpaid holds expire and seats return to AVAILABLE.
- Verify cancellation applies the configured refund policy.
- Verify README and API walkthrough are included.
