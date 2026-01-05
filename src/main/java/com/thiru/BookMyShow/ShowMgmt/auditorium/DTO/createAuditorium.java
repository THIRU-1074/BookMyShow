package com.thiru.BookMyShow.ShowMgmt.auditorium.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
public class createAuditorium {
    @NotBlank(message = "Auditorium name is required")
    private String auditoriumName;

    @NotNull(message = "venueId is required")
    private Long venueId;

    private String userName;
}
