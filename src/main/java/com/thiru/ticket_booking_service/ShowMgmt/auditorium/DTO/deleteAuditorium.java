package com.thiru.ticket_booking_service.ShowMgmt.auditorium.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class deleteAuditorium {

    @NotBlank(message = "Auditorium Id is required")
    private Long auditoriumId;

    private String userName;
}
