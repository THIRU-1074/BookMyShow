package com.thiru.ticket_booking_service.ShowMgmt.auditorium.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class createAuditorium {
    @NotBlank(message = "Auditorium name is required")
    private String auditoriumName;

    @NotBlank(message = "venueId is required")
    private Long venueId;

    private String userName;
}
