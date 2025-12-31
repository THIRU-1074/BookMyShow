package com.thiru.BookMyShow.userMgmt;

import org.springframework.web.bind.annotation.*;

import com.thiru.BookMyShow.appSecurity.*;
import com.thiru.BookMyShow.userMgmt.DTO.UserLoginRequestDTO;
import com.thiru.BookMyShow.userMgmt.DTO.UserProfileResponseDTO;
import com.thiru.BookMyShow.userMgmt.DTO.UserSignupRequestDTO;

import org.springframework.http.*;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @Valid @RequestBody UserSignupRequestDTO request) {

        userService.signup(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        UserLoginRequestDTO request = new UserLoginRequestDTO();
        // 1. Resolve auth type and token from header
        authService.resolveAuthHeader(authorizationHeader, request);
        AuthResponseDTO tokens = userService.login(request);
        // 2. Front-end must annex this access token to every header
        return ResponseEntity.ok(tokens);
    }

    @PatchMapping("/updateProfile")
    public ResponseEntity<?> updateUserProfile(Authentication authentication) {
        // Update the fields available in Request Body
        return getUserProfile(authentication);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(
            Authentication authentication) {

        Claims claims = (Claims) authentication.getPrincipal();
        String userName = claims.getSubject();
        UserProfileResponseDTO response = userService.getUserProfile(userName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public void viewHomePage() {
        // It must have login,signup button for sure
        /*
         * Onclick
         * ->signin--It must send jwt if available in cookie, or if jwt(refresh token)
         * expired,else open basic login view
         * --After jwt authentication store the access token and send them for every
         * request
         * --If authenticated with basic login store jwt(refresh token in cookie) and
         * use access token
         */
    }

    @GetMapping("/login")
    public String loginView() {
        return "Login page placeholder";
    }

    @GetMapping("/signup")
    public String registerView() {
        return "Register page placeholder";
    }
}
