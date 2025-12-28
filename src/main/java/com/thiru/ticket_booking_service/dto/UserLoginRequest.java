package com.thiru.ticket_booking_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class UserLoginRequest {

    private String userName;

    // Used only for BASIC auth
    private String password;

    // Used only for JWT auth
    private String accessToken;

    private AuthType authType;
}

