package com.thiru.BookMyShow.ShowMgmt.auditorium.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class deleteAuditorium {

    @NotBlank(message = "Auditorium Id is required")
    private Long auditoriumId;

    @NotBlank(message = "userName is required...!")
    private String userName;
}
