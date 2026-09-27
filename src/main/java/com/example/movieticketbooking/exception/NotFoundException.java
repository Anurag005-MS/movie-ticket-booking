package com.example.movieticketbooking.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String m) {
        super(m);
    }
}
