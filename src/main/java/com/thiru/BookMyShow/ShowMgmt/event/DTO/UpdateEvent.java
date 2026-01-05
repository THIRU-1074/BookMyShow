package com.thiru.BookMyShow.ShowMgmt.event.DTO;

import com.thiru.BookMyShow.ShowMgmt.event.EventType;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
public class UpdateEvent {
    @NotNull(message = "event Id is required to update")
    private Long eventId;

    private EventType eventType;

    private String eventName;

    private String userName;
}
