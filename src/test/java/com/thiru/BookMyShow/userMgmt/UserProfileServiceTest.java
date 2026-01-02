package com.thiru.BookMyShow.userMgmt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thiru.BookMyShow.appSecurity.AuthResponseDTO;
import com.thiru.BookMyShow.appSecurity.AuthService;
import com.thiru.BookMyShow.appSecurity.AuthType;
import com.thiru.BookMyShow.bookingMgmt.BookingService;
import com.thiru.BookMyShow.bookingMgmt.DTO.ReadBookingResponse;
import com.thiru.BookMyShow.bookingMgmt.DTO.ReadBookings;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;
import com.thiru.BookMyShow.userMgmt.DTO.UserLoginRequestDTO;
import com.thiru.BookMyShow.userMgmt.DTO.UserSignupRequestDTO;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthService authService;
    @Mock
    private BookingService bookingService;

    private UserProfileService userProfileService;

    @BeforeEach
    void setUp() {
        userProfileService = new UserProfileService(userRepository, authService, bookingService);
    }

    @Test
    void loginWithBasicCredentialsReturnsTokens() {
        UserLoginRequestDTO request = new UserLoginRequestDTO();
        request.setAuthType(AuthType.BASIC);
        request.setUserName("jane");
        request.setCredential("raw-password");

        UserEntity user = BookingTestDataFactory.user(1L, "jane", Role.USER);
        when(userRepository.findByUserName("jane")).thenReturn(Optional.of(user));
        when(authService.verifyPassword("raw-password", user.getHashedPwd())).thenReturn(true);
        when(authService.generateRefreshToken(user)).thenReturn("refresh");
        when(authService.generateAccessToken("refresh")).thenReturn("access");

        AuthResponseDTO response = userProfileService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access");
        assertThat(response.getRefreshToken()).isEqualTo("refresh");
    }

    @Test
    void loginWithJwtCredentialRequiresValidToken() {
        UserLoginRequestDTO request = new UserLoginRequestDTO();
        request.setAuthType(AuthType.JWT);
        request.setCredential("jwt-token");

        when(authService.isTokenExpired("jwt-token")).thenReturn(false);
        when(authService.isTokenValid("jwt-token")).thenReturn(true);
        when(authService.generateAccessToken("jwt-token")).thenReturn("access");

        AuthResponseDTO response = userProfileService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access");
        assertThat(response.getRefreshToken()).isEqualTo("jwt-token");
    }

    @Test
    void loginThrowsWhenJwtExpired() {
        UserLoginRequestDTO request = new UserLoginRequestDTO();
        request.setAuthType(AuthType.JWT);
        request.setCredential("expired");

        when(authService.isTokenExpired("expired")).thenReturn(true);

        assertThatThrownBy(() -> userProfileService.login(request))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void signupValidatesUniqueEmail() {
        UserSignupRequestDTO request = new UserSignupRequestDTO();
        request.setMailId("user@test.com");
        request.setPhoneNumber("9999999999");
        request.setName("test");
        request.setPassword("password");
        request.setRoleEnum(Role.USER);

        when(userRepository.existsByMailId("user@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userProfileService.signup(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getUserProfileReturnsBookings() {
        UserEntity user = BookingTestDataFactory.user(1L, "jane", Role.USER);
        when(userRepository.findByUserName("jane")).thenReturn(Optional.of(user));

        List<ReadBookingResponse> bookings = List.of(ReadBookingResponse.builder().bookingId(5L).build());
        when(bookingService.readBookings(org.mockito.ArgumentMatchers.any(ReadBookings.class))).thenReturn(bookings);

        var response = userProfileService.getUserProfile("jane");

        assertThat(response.getBookings()).hasSize(1);

        ArgumentCaptor<ReadBookings> captor = ArgumentCaptor.forClass(ReadBookings.class);
        verify(bookingService).readBookings(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("jane");
    }
}
