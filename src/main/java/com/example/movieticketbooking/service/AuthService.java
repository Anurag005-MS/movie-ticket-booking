package com.example.movieticketbooking.service;

import com.example.movieticketbooking.dto.ApiDtos.*;
import com.example.movieticketbooking.entity.*;
import com.example.movieticketbooking.exception.*;
import com.example.movieticketbooking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest r) {
        if (users.findByEmailIgnoreCase(r.email()).isPresent()) throw new ConflictException("Email already registered");
        User u = users.save(new User(r.name(), r.email().toLowerCase(), encoder.encode(r.password()), Role.CUSTOMER));
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole());
    }
}
