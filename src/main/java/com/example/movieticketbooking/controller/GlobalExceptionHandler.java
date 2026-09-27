package com.example.movieticketbooking.controller;

import com.example.movieticketbooking.dto.ApiDtos.ErrorResponse;
import com.example.movieticketbooking.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ErrorResponse> e(HttpStatus s, String c, String m) {
        return ResponseEntity.status(s).body(new ErrorResponse(c, m, LocalDateTime.now()));
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ErrorResponse> nf(NotFoundException x) {
        return e(HttpStatus.NOT_FOUND, "NOT_FOUND", x.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    ResponseEntity<ErrorResponse> br(BadRequestException x) {
        return e(HttpStatus.BAD_REQUEST, "BAD_REQUEST", x.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    ResponseEntity<ErrorResponse> cf(ConflictException x) {
        return e(HttpStatus.CONFLICT, "CONFLICT", x.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    ResponseEntity<ErrorResponse> fb(ForbiddenException x) {
        return e(HttpStatus.FORBIDDEN, "FORBIDDEN", x.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> val(MethodArgumentNotValidException x) {
        String m = x.getBindingResult().getFieldErrors().stream().map(a -> a.getField() + ": " + a.getDefaultMessage()).collect(Collectors.joining(", "));
        return e(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", m);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ErrorResponse> cv(ConstraintViolationException x) {
        return e(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", x.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ErrorResponse> di(DataIntegrityViolationException x) {
        return e(HttpStatus.CONFLICT, "DATA_CONFLICT", "Request conflicts with existing data");
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> generic(Exception x) {
        return e(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected server error");
    }
}
