package com.thiru.ticket_booking_service.ShowMgmt.event.DTO;

import lombok.*;

import com.thiru.ticket_booking_service.ShowMgmt.event.*;

@Getter
@Setter
@Builder
public class EventReadResponse {
    private EventType eventType;
    private String eventName;
    private Long eventId;
}
