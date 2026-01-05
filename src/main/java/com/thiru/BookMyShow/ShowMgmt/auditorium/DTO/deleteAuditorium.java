package com.thiru.BookMyShow.ShowMgmt.auditorium.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
public class deleteAuditorium {

    @NotNull(message = "Auditorium Id is required")
    private Long auditoriumId;

    private String userName;
}
