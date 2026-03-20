package com.example.aichatbot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Get Authorization header
        String authHeader = request
            .getHeader("Authorization");

        String token = null;
        String username = null;

        // Step 2: Extract token from "Bearer <token>"
        if (authHeader != null && 
                authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtUtil.extractUsername(token);
        }

        // Step 3: Validate token & set authentication
        if (username != null && SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {

            UserDetails userDetails = 
                userDetailsService
                    .loadUserByUsername(username);

            if (jwtUtil.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null,
                        userDetails.getAuthorities());

                authToken.setDetails(
                    new WebAuthenticationDetailsSource()
                        .buildDetails(request));

                SecurityContextHolder.getContext()
                    .setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}