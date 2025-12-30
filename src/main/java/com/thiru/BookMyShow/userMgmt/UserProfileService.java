package com.thiru.BookMyShow.userMgmt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thiru.BookMyShow.appSecurity.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public AuthResponseDTO login(UserLoginRequestDTO request) {
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

    private AuthResponseDTO handleBasicLogin(UserLoginRequestDTO request, UserEntity user) {

        boolean valid = authService.verifyPassword(
                request.getPassword(),
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
        // return json of both
    }

    private AuthResponseDTO handleJwtLogin(UserLoginRequestDTO request) {

        if (authService.isTokenExpired(request.getPassword())) {
            throw new RuntimeException("401 UNAUTHORIZED - Token expired");
        }

        if (!authService.isTokenValid(request.getPassword())) {
            throw new RuntimeException("401 UNAUTHORIZED - Invalid token");
        }

        // Generate both tokens
        String rtoken = request.getPassword();
        String atoken = authService.generateAccessToken(rtoken);

        AuthResponseDTO response = AuthResponseDTO.builder()
                .accessToken(atoken)
                .refreshToken(rtoken)
                .build();

        return response;
    }

    @Transactional
    public void signup(UserSignupRequestDTO request) {
        if (request.getRole().equals("ADMIN"))
            request.setRoleEnum(Role.ADMIN);
        else if (request.getRole().equals("USER"))
            request.setRoleEnum(Role.USER);
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
                .role(request.getRoleEnum())
                .build();

        // 3️⃣ Persist
        userRepository.save(user);
    }

    public UserProfileResponseDTO getUserProfile(String userName) {

        UserEntity user = userRepository.findByName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserBookingSummaryDTO> bookingDtos = user.getBookings()
                .stream()
                .map(booking -> UserBookingSummaryDTO.builder()
                        .bookingId(booking.getBookingId())
                        .showId(booking.getShowSeat().getShow().getShowId())
                        .eventId(booking.getShowSeat().getShow().getEvent().getEventId())
                        .seatNo(booking.getShowSeat().getSeat().getSeatNo())
                        .seatCategory(booking.getShowSeat().getCategory().getName())
                        .build())
                .toList();

        return UserProfileResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .mailId(user.getMailId())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .bookings(bookingDtos)
                .build();
    }
}
