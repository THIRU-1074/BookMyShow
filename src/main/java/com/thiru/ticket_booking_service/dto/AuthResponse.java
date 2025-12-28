package com.thiru.ticket_booking_service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
}

