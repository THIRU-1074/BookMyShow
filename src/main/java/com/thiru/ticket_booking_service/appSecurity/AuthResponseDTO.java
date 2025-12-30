package com.thiru.ticket_booking_service.appSecurity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseDTO {

    private String accessToken;
    private String refreshToken;
}
