package com.thiru.ticket_booking_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class UserLoginRequest {

    @NotBlank
    private String mailId;

    // Used only for BASIC auth
    private String password;

    // Used only for JWT auth
    private String accessToken;

    @NotBlank
    private AuthType authType;
}

