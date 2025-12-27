package com.thiru.ticket_booking_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String mailId;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Size(min = 6)
    private String password;
}

