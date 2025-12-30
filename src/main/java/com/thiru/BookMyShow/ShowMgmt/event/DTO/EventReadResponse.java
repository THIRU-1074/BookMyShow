package com.thiru.BookMyShow.ShowMgmt.event.DTO;

import com.thiru.BookMyShow.ShowMgmt.event.*;

import lombok.*;

@Getter
@Setter
@Builder
public class EventReadResponse {
    private EventType eventType;
    private String eventName;
    private Long eventId;
}
