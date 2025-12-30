package com.thiru.BookMyShow.ShowMgmt.event.DTO;

import com.thiru.BookMyShow.ShowMgmt.event.EventType;

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