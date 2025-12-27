package com.thiru.ticket_booking_service.service;

import com.thiru.ticket_booking_service.repository.*;
import com.thiru.ticket_booking_service.dto.*;
import com.thiru.ticket_booking_service.entity.*;
import com.thiru.ticket_booking_service.service.securityServices.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final AuthService authService;
    public String login(UserLoginRequest request) {

        UserEntity user = userRepository.findByMailId(request.getMailId())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (request.getAuthType() == AuthType.BASIC) {
            return handleBasicLogin(request, user);
        }

        if (request.getAuthType() == AuthType.JWT) {
            return handleJwtLogin(request);
        }

        throw new RuntimeException("Unsupported auth type");
    }

    private String handleBasicLogin(UserLoginRequest request, UserEntity user) {

        boolean valid = authService.verifyPassword(
                request.getPassword(),
                user.getHashedPwd()
        );

        if (!valid) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate fresh access token
        return authService.generateAccessToken(user);
    }

    private String handleJwtLogin(UserLoginRequest request) {

        if (authService.isTokenExpired(request.getAccessToken())) {
            throw new RuntimeException("401 UNAUTHORIZED - Token expired");
        }

        if (!authService.isTokenValid(request.getAccessToken())) {
            throw new RuntimeException("401 UNAUTHORIZED - Invalid token");
        }

        // Token is valid → issue a new access token
        return authService.refreshAccessToken(request.getAccessToken());
    }
    @Transactional
    public void signup(UserSignupRequest request) {

        // 1️⃣ Validate uniqueness
        if (userRepository.existsByMailId(request.getMailId())) {
            throw new IllegalArgumentException("Email already registered");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already registered");
        }

        // 2️⃣ Map DTO → Entity
        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .mailId(request.getMailId())
                .phoneNumber(request.getPhoneNumber())
                .hashedPwd(authService.hash(request.getPassword()))
                .role(Role.USER)
                .build();

        // 3️⃣ Persist
        userRepository.save(user);
    }

        public UserProfileResponse getUserProfile(String email) {

        UserEntity user = userRepository.findByMailId(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserBookingSummary> bookingDtos = user.getBookings()
                .stream()
                .map(booking -> UserBookingSummary.builder()
                        .bookingId(booking.getBookingId())
                        .eventId(booking.getEventId())
                        .showId(booking.getShowId())
                        .seatNo(booking.getSeatNo())
                        .seatCategory(booking.getSeatCategory().name())
                        .build())
                .toList();

        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .mailId(user.getMailId())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .bookings(bookingDtos)
                .build();
    }
}

