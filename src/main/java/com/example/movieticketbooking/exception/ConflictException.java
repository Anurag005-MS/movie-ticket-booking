package com.example.movieticketbooking.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String m) {
        super(m);
    }
}
