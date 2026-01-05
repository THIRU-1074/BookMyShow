package com.thiru.BookMyShow.ShowMgmt.event.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
public class DeleteEvent {

    @NotNull(message = "Event Id is required")
    private Long eventId;

    private String userName;
}
