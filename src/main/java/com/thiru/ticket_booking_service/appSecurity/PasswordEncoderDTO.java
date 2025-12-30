package com.thiru.ticket_booking_service.appSecurity;

public interface PasswordEncoderDTO {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
