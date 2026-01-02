package com.thiru.BookMyShow.appSecurity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thiru.BookMyShow.userMgmt.DTO.UserLoginRequestDTO;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PasswordEncoderDTO passwordEncoder;
    @Mock
    private JwtPropertiesDTO jwtProperties;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(passwordEncoder, jwtProperties);
    }

    @Test
    void resolveAuthHeaderParsesBasicCredentials() {
        UserLoginRequestDTO dto = new UserLoginRequestDTO();

        authService.resolveAuthHeader("Basic amFuZTpzZWNyZXQ=", dto);

        assertThat(dto.getAuthType()).isEqualTo(AuthType.BASIC);
        assertThat(dto.getUserName()).isEqualTo("jane");
        assertThat(dto.getCredential()).isEqualTo("secret");
    }

    @Test
    void resolveAuthHeaderParsesBearerToken() {
        UserLoginRequestDTO dto = new UserLoginRequestDTO();

        authService.resolveAuthHeader("Bearer token-123", dto);

        assertThat(dto.getAuthType()).isEqualTo(AuthType.JWT);
        assertThat(dto.getCredential()).isEqualTo("token-123");
    }
}
