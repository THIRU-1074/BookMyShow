package com.thiru.ticket_booking_service.ShowMgmt.auditorium.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class updateAuditorium {
    @NotBlank(message = "Auditorium name is required")
    private String auditoriumName;

    @NotBlank(message = "Auditorium Id is required")
    private Long auditoriumId;

    private String userName;
}
