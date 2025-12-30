package com.thiru.ticket_booking_service.ShowMgmt.event.DTO;

import com.thiru.ticket_booking_service.ShowMgmt.event.EventType;

import lombok.*;

@Getter
@Setter
public class ReadEvent {
    private String eventType;

    private EventType eventTypeEnum;

    private String eventName;

    private Long eventId;

    private String userName;

}