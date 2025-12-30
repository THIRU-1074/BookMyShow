package com.thiru.ticket_booking_service.ShowMgmt.show.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class DeleteShow {

    @NotBlank(message = "Show id is required...!")
    private Long showId;

    @NotBlank(message = "User name is required...!")
    private String userName;
}
