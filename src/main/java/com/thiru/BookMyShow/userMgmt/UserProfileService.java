package com.thiru.BookMyShow.userMgmt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

import com.thiru.BookMyShow.appSecurity.*;
import com.thiru.BookMyShow.bookingMgmt.BookingService;
import com.thiru.BookMyShow.bookingMgmt.DTO.*;
import com.thiru.BookMyShow.userMgmt.DTO.UserLoginRequestDTO;
import com.thiru.BookMyShow.userMgmt.DTO.UserProfileResponseDTO;
import com.thiru.BookMyShow.userMgmt.DTO.UserSignupRequestDTO;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final BookingService bookingService;

    public AuthResponseDTO login(UserLoginRequestDTO request) {
        if (request.getAuthType() == AuthType.BASIC) {
            UserEntity user = userRepository.findByUserName(request.getUserName())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));
            return handleBasicLogin(request, user);
        }

        else if (request.getAuthType() == AuthType.JWT) {
            return handleJwtLogin(request);
        }

        throw new RuntimeException("Unsupported auth type");
    }

    private AuthResponseDTO handleBasicLogin(UserLoginRequestDTO request, UserEntity user) {

        boolean valid = authService.verifyPassword(
                request.getCredential(),
                user.getHashedPwd());

        if (!valid) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate both tokens
        String rtoken = authService.generateRefreshToken(user);
        String atoken = authService.generateAccessToken(rtoken);

        AuthResponseDTO response = AuthResponseDTO.builder()
                .accessToken(atoken)
                .refreshToken(rtoken)
                .build();

        return response;
    }

    private AuthResponseDTO handleJwtLogin(UserLoginRequestDTO request) {

        if (authService.isTokenExpired(request.getCredential())) {
            throw new RuntimeException("401 UNAUTHORIZED - Token expired");
        }

        if (!authService.isTokenValid(request.getCredential())) {
            throw new RuntimeException("401 UNAUTHORIZED - Invalid token");
        }

        // Generate both tokens
        String rtoken = request.getCredential();
        String atoken = authService.generateAccessToken(rtoken);

        AuthResponseDTO response = AuthResponseDTO.builder()
                .accessToken(atoken)
                .refreshToken(rtoken)
                .build();

        return response;
    }

    @Transactional
    public void signup(UserSignupRequestDTO request) {
        // 1️⃣ Validate uniqueness
        if (userRepository.existsByMailId(request.getMailId())) {
            throw new IllegalArgumentException("Email already registered");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already registered");
        }

        // 2️⃣ Map DTO → Entity
        UserEntity user = UserEntity.builder()
                .userName(request.getName())
                .mailId(request.getMailId())
                .phoneNumber(request.getPhoneNumber())
                .hashedPwd(authService.hash(request.getPassword()))
                .role(request.getRoleEnum())
                .build();

        // 3️⃣ Persist
        userRepository.save(user);
    }

    public UserProfileResponseDTO getUserProfile(String userName) {

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔹 Build ReadBookings filter using userName
        ReadBookings readBookings = ReadBookings.builder()
                .userName(userName)
                .build();

        // 🔹 Fetch booking DTOs
        List<ReadBookingResponse> bookingDtos = bookingService.readBookings(readBookings);

        return UserProfileResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getUserName())
                .mailId(user.getMailId())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .bookings(bookingDtos)
                .build();
    }
}
