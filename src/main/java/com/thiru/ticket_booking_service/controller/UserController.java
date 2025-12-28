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
    public ResponseEntity<AuthResponse> login(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorizationHeader){
        UserLoginRequest request=new UserLoginRequest();
        // 1. Resolve auth type and token from header
        authService.resolveAuthHeader(authorizationHeader,request);
        AuthResponse tokens = userService.login(request);
        //2. Front-end must annex this access token to every header
        return ResponseEntity.ok(tokens);
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

            String userName = claims.getSubject();

            // 4️⃣ Delegate to service
            UserProfileResponse response = userService.getUserProfile(userName);

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
    /*@GetMapping("/")
    public void viewHomePage(){
        //It must have login,signup button for sure
        /*Onclick 
            ->signin--It must send jwt if available in cookie, or if jwt(refresh token) expired,else open basic login view
                    --After jwt authentication store the access token and send them for every request
                    --If authenticated with basic login store jwt(refresh token in cookie) and use access token
        
    }*/
    @GetMapping("/login")
    public String loginView() {
        return "Login page placeholder";
    }

    @GetMapping("/signup")
    public String registerView() {
        return "Register page placeholder";
    }
}

