CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE city (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE theater (
    id UUID PRIMARY KEY,
    city_id UUID NOT NULL REFERENCES city(id),
    name VARCHAR(160) NOT NULL,
    address VARCHAR(300),
    CONSTRAINT uq_theater_city_name UNIQUE(city_id, name)
);

CREATE TABLE movie (
    id UUID PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    duration_minutes INT NOT NULL,
    language VARCHAR(80) NOT NULL,
    genre VARCHAR(120),
    release_date DATE
);

CREATE TABLE movie_show (
    id UUID PRIMARY KEY,
    movie_id UUID NOT NULL REFERENCES movie(id),
    theater_id UUID NOT NULL REFERENCES theater(id),
    starts_at TIMESTAMP NOT NULL,
    ends_at TIMESTAMP NOT NULL,
    CONSTRAINT chk_show_time CHECK (ends_at > starts_at)
);

CREATE TABLE seat (
    id UUID PRIMARY KEY,
    theater_id UUID NOT NULL REFERENCES theater(id),
    row_label VARCHAR(10) NOT NULL,
    seat_number INT NOT NULL,
    seat_type VARCHAR(30) NOT NULL,
    CONSTRAINT uq_seat UNIQUE(theater_id, row_label, seat_number)
);

CREATE TABLE pricing_tier (
    id UUID PRIMARY KEY,
    name VARCHAR(60) NOT NULL UNIQUE,
    seat_type VARCHAR(30) NOT NULL,
    day_type VARCHAR(30) NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE discount_code (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    percentage NUMERIC(5,2) NOT NULL,
    max_discount NUMERIC(12,2),
    valid_from TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE refund_policy (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    hours_before_show INT NOT NULL,
    refund_percentage NUMERIC(5,2) NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE show_seat (
    id UUID PRIMARY KEY,
    show_id UUID NOT NULL REFERENCES movie_show(id),
    seat_id UUID NOT NULL REFERENCES seat(id),
    status VARCHAR(30) NOT NULL,
    held_by UUID REFERENCES app_user(id),
    held_until TIMESTAMP,
    CONSTRAINT uq_show_seat UNIQUE(show_id, seat_id)
);

CREATE TABLE booking (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_user(id),
    show_id UUID NOT NULL REFERENCES movie_show(id),
    status VARCHAR(30) NOT NULL,
    subtotal NUMERIC(12,2) NOT NULL,
    discount_amount NUMERIC(12,2) NOT NULL,
    total_amount NUMERIC(12,2) NOT NULL,
    discount_code VARCHAR(50),
    expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    reminder_sent BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE booking_seat (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL REFERENCES booking(id),
    show_seat_id UUID NOT NULL REFERENCES show_seat(id),
    unit_price NUMERIC(12,2) NOT NULL,
    CONSTRAINT uq_booking_show_seat UNIQUE(booking_id, show_seat_id)
);

CREATE TABLE payment (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL UNIQUE REFERENCES booking(id),
    provider VARCHAR(50) NOT NULL,
    provider_reference VARCHAR(120),
    status VARCHAR(30) NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    paid_at TIMESTAMP
);

CREATE INDEX idx_theater_city ON theater(city_id);
CREATE INDEX idx_show_theater_start ON movie_show(theater_id, starts_at);
CREATE INDEX idx_show_movie_start ON movie_show(movie_id, starts_at);
CREATE INDEX idx_show_seat_status ON show_seat(show_id, status);
CREATE INDEX idx_booking_user_created ON booking(user_id, created_at DESC);
CREATE INDEX idx_booking_expiry ON booking(status, expires_at);
