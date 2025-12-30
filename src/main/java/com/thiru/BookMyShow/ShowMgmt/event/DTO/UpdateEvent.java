package com.thiru.BookMyShow.ShowMgmt.event.DTO;

import com.thiru.BookMyShow.ShowMgmt.event.EventType;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class UpdateEvent {
    @NotBlank(message = "event Id is required to update")
    private Long eventId;

    private String eventType;

    private EventType eventTypeEnum;

    private String eventName;

    @NotBlank(message = "User name is required")
    private String userName;
}
