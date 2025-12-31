package com.thiru.BookMyShow.ShowMgmt.event.DTO;

import com.thiru.BookMyShow.ShowMgmt.event.EventType;

import lombok.*;

@Getter
@Setter
public class ReadEvent {
    private EventType eventType;

    private String eventName;

    private Long eventId;

    private String userName;

}