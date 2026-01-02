package com.thiru.BookMyShow.userMgmt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
// THESE ARE NEW (Spring Boot 4.x)
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; // Check exact IDE suggestion
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Replaces @MockBean
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thiru.BookMyShow.appSecurity.AuthResponseDTO;
import com.thiru.BookMyShow.appSecurity.AuthService;
import com.thiru.BookMyShow.appSecurity.AuthType;
import com.thiru.BookMyShow.userMgmt.DTO.UserLoginRequestDTO;
import com.thiru.BookMyShow.userMgmt.DTO.UserProfileResponseDTO;
import com.thiru.BookMyShow.userMgmt.DTO.UserSignupRequestDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserProfileService userProfileService;
    @MockitoBean
    private AuthService authService;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authentication = buildAuthentication("jane");
    }

    @Test
    void signupEndpointDelegatesToService() throws Exception {
        UserSignupRequestDTO request = new UserSignupRequestDTO();
        request.setName("Jane");
        request.setMailId("jane@test.com");
        request.setPhoneNumber("9999999999");
        request.setRoleEnum(Role.USER);
        request.setPassword("secret1");

        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        ArgumentCaptor<UserSignupRequestDTO> captor = ArgumentCaptor.forClass(UserSignupRequestDTO.class);
        verify(userProfileService).signup(captor.capture());
        assertThat(captor.getValue().getMailId()).isEqualTo("jane@test.com");
    }

    @Test
    void loginEndpointResolvesAuthorizationHeader() throws Exception {
        doAnswer(invocation -> {
            UserLoginRequestDTO dto = invocation.getArgument(1);
            dto.setAuthType(AuthType.BASIC);
            dto.setUserName("jane");
            dto.setCredential("secret");
            return null;
        }).when(authService).resolveAuthHeader(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());

        when(userProfileService.login(org.mockito.ArgumentMatchers.any()))
                .thenReturn(AuthResponseDTO.builder().accessToken("access").refreshToken("refresh").build());

        mockMvc.perform(post("/users/login")
                .header("Authorization", "Basic amFuZTpzZWNyZXQ="))
                .andExpect(status().isOk());

        ArgumentCaptor<UserLoginRequestDTO> captor = ArgumentCaptor.forClass(UserLoginRequestDTO.class);
        verify(userProfileService).login(captor.capture());
        assertThat(captor.getValue().getAuthType()).isEqualTo(AuthType.BASIC);
        assertThat(captor.getValue().getUserName()).isEqualTo("jane");
    }

    @Test
    void profileEndpointUsesAuthenticatedUser() throws Exception {
        when(userProfileService.getUserProfile("jane"))
                .thenReturn(UserProfileResponseDTO.builder().userId(1L).name("jane").build());

        mockMvc.perform(get("/users/profile").principal(authentication))
                .andExpect(status().isOk());

        verify(userProfileService).getUserProfile("jane");
    }

    private Authentication buildAuthentication(String username) {
        Claims claims = new DefaultClaims(Map.of());
        claims.setSubject(username);
        return new TestingAuthenticationToken(claims, null);
    }
}
