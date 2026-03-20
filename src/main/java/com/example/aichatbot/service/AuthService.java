package com.example.aichatbot.service;

import com.example.aichatbot.dto.*;
import com.example.aichatbot.entity.User;
import com.example.aichatbot.repository.UserRepository;
import com.example.aichatbot.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {

        // Check if username already exists
        if (userRepository.existsByUsername(
                request.getUsername())) {
            throw new RuntimeException(
                "Username already exists!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(
                request.getEmail())) {
            throw new RuntimeException(
                "Email already exists!");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // Encode password before saving! Never store plain text!
        user.setPassword(passwordEncoder.encode(
            request.getPassword()));

        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(
            request.getUsername());

        return new AuthResponse(token, 
            request.getUsername(), 
            "Registration successful!");
    }

    public AuthResponse login(AuthRequest request) {

        // Spring validates username + password
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()));

        // If no exception → credentials valid!
        String token = jwtUtil.generateToken(
            request.getUsername());

        return new AuthResponse(token,
            request.getUsername(),
            "Login successful!");
    }
}