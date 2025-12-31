package com.thiru.BookMyShow.ShowMgmt.auditorium.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class updateAuditorium {
    @NotBlank(message = "Auditorium name is required")
    private String auditoriumName;

    @NotBlank(message = "Auditorium Id is required")
    private Long auditoriumId;

    @NotBlank(message = "UserName is required")
    private String userName;
}
