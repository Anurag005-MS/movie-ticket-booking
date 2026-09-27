# Endpoint reference

All `/api/v1/**` endpoints except registration require HTTP Basic authentication. `/api/v1/admin/**` requires ADMIN; customer operations require an authenticated user.

| Method | Endpoint | Role | Purpose |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Public | Register customer |
| GET | `/api/v1/cities` | Auth | Browse cities |
| GET | `/api/v1/movies` | Auth | Browse movies |
| GET | `/api/v1/theaters?cityId=` | Auth | Browse theaters |
| GET | `/api/v1/shows?...` | Auth | Search shows |
| GET | `/api/v1/shows/{showId}/seats` | Auth | Live show seat map |
| POST | `/api/v1/shows/{showId}/holds` | Customer | Hold seats + create pending booking |
| POST | `/api/v1/bookings/{bookingId}/payment/success` | Customer | Simulated successful payment |
| POST | `/api/v1/bookings/{bookingId}/cancel` | Customer | Cancel booking + refund if applicable |
| GET | `/api/v1/bookings` | Customer | Booking history |
| GET | `/api/v1/bookings/{bookingId}` | Customer | Own booking details |
| GET/POST/PUT/DELETE | `/api/v1/admin/cities` | Admin | Manage cities |
| GET/POST/PUT/DELETE | `/api/v1/admin/theaters` | Admin | Manage theaters |
| GET/POST/PUT/DELETE | `/api/v1/admin/movies` | Admin | Manage movies |
| POST/PUT/DELETE | `/api/v1/admin/shows` | Admin | Manage shows |
| GET/POST/PUT/DELETE | `/api/v1/admin/theaters/{theaterId}/seats`, `/api/v1/admin/seats` | Admin | Manage seat layouts |
| GET/POST/PUT/DELETE | `/api/v1/admin/pricing-tiers` | Admin | Manage pricing |
| GET/POST/PUT/DELETE | `/api/v1/admin/discount-codes` | Admin | Manage discounts |
| GET/POST/PUT/DELETE | `/api/v1/admin/refund-policies` | Admin | Manage refund rules |
