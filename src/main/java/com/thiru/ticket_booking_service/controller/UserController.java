package com.thiru.ticket_booking_service.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.*;
import java.util.*;

import com.thiru.ticket_booking_service.service.securityServices.*;
import com.thiru.ticket_booking_service.dto.*;
import com.thiru.ticket_booking_service.service.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userService;
    private final AuthService authService;
    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @Valid @RequestBody UserSignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @Valid @RequestBody UserLoginRequest request) {

        try {
            String accessToken = userService.login(request);
            return ResponseEntity.ok(accessToken);
            //The front-end must store it in Http-only cookies🍪🍪🍪& send them with each request.

        } catch (RuntimeException ex) {
            // Authentication / authorization failure
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ex.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(
            @RequestHeader("Authorization") String authorizationHeader) {

        // 1️⃣ Check presence of token
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or invalid Authorization header");
        }

        try {
            // 2️⃣ Extract token
            String token = authorizationHeader.substring(7);

            // 3️⃣ AUTHORIZE (before calling service)
            Claims claims = authService.verifyAccessToken(token);

            String email = claims.getSubject();

            // 4️⃣ Delegate to service
            UserProfileResponse response = userService.getUserProfile(email);

            return ResponseEntity.ok(response);

        } catch (ExpiredJwtException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Access token expired");

        } catch (JwtException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid access token");
        }
    }

}

