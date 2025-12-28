package com.thiru.ticket_booking_service.service;

import com.thiru.ticket_booking_service.repository.*;
import com.thiru.ticket_booking_service.dto.*;
import com.thiru.ticket_booking_service.entity.*;
import com.thiru.ticket_booking_service.entity.enums.Role;
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
    public AuthResponse login(UserLoginRequest request) {
        if (request.getAuthType() == AuthType.BASIC) {
            UserEntity user = userRepository.findByName(request.getUserName())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
            return handleBasicLogin(request, user);
        }

        else if (request.getAuthType() == AuthType.JWT) {
            return handleJwtLogin(request);
        }

        throw new RuntimeException("Unsupported auth type");
    }

    private AuthResponse handleBasicLogin(UserLoginRequest request, UserEntity user) {

        boolean valid = authService.verifyPassword(
                request.getPassword(),
                user.getHashedPwd()
        );

        if (!valid) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate both tokens
        String rtoken = authService.generateRefreshToken(user);
        String atoken = authService.generateAccessToken(rtoken);

        AuthResponse response = AuthResponse.builder()
            .accessToken(atoken)
            .refreshToken(rtoken)
            .build();

        return response;
        //return json of both 
    }

    private AuthResponse handleJwtLogin(UserLoginRequest request) {

        if (authService.isTokenExpired(request.getPassword())) {
            throw new RuntimeException("401 UNAUTHORIZED - Token expired");
        }

        if (!authService.isTokenValid(request.getPassword())) {
            throw new RuntimeException("401 UNAUTHORIZED - Invalid token");
        }

        // Generate both tokens
        String rtoken = request.getPassword();
        String atoken = authService.generateAccessToken(rtoken);

        AuthResponse response = AuthResponse.builder()
            .accessToken(atoken)
            .refreshToken(rtoken)
            .build();

        return response;
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

    public UserProfileResponse getUserProfile(String userName) {

        UserEntity user = userRepository.findByName(userName)
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

