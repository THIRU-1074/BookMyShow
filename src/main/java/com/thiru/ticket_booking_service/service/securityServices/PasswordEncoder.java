package com.thiru.ticket_booking_service.service.securityServices;

public interface PasswordEncoder {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}

