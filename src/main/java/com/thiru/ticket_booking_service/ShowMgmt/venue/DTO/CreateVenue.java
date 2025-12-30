package com.thiru.ticket_booking_service.ShowMgmt.venue.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
public class CreateVenue {
    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "\\d{6}", message = "Pincode must be 6 digits")
    private String pincode;

    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;

    private String addressLine2;

    private String landmark;

    @NotBlank(message = "username is required")
    private String userName;
}
