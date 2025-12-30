package com.thiru.BookMyShow.ShowMgmt.event.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class DeleteEvent {

    @NotBlank(message = "Event Id is required")
    private Long eventId;

    @NotBlank(message = "User name is required")
    private String userName;
}
