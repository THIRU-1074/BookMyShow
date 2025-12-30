package com.thiru.BookMyShow.ShowMgmt.event.DTO;

import com.thiru.BookMyShow.ShowMgmt.event.EventType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
public class CreateEvent {
    @NotNull(message = "Event type is required")
    private String eventType;

    private EventType eventTypeEnum;

    @NotBlank(message = "Event name is required")
    private String eventName;

    @NotBlank(message = "User name is required")
    private String userName;
}
