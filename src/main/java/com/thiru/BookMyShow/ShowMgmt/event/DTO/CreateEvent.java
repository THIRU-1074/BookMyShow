package com.thiru.BookMyShow.ShowMgmt.event.DTO;

import com.thiru.BookMyShow.ShowMgmt.event.EventType;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class CreateEvent {
    private EventType eventType;

    @NotBlank(message = "Event name is required")
    private String eventName;

    private String userName;
}
