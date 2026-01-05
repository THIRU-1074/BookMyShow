package com.thiru.BookMyShow.ShowMgmt.auditorium.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
public class updateAuditorium {
    @NotBlank(message = "Auditorium name is required")
    private String auditoriumName;

    @NotNull(message = "Auditorium Id is required")
    private Long auditoriumId;

    private String userName;
}
