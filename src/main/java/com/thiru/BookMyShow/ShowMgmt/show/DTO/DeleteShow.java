package com.thiru.BookMyShow.ShowMgmt.show.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
public class DeleteShow {

    @NotNull(message = "Show id is required...!")
    private Long showId;

    private String userName;
}
