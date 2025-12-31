package com.thiru.BookMyShow.ShowMgmt.venue.DTO;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@Builder
public class ReadVenue {
    private Long venueId;

    private String city;
    @Pattern(regexp = "\\d{6}", message = "Pincode must be 6 digits")
    private String pincode;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
}
