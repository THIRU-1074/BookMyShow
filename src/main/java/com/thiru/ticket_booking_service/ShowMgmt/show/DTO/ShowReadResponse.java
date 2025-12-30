package com.thiru.ticket_booking_service.ShowMgmt.show.DTO;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;

import com.thiru.ticket_booking_service.ShowMgmt.event.*;
import com.thiru.ticket_booking_service.ShowMgmt.show.*;

@Setter
@Getter
@Builder
public class ShowReadResponse {

    private Long showId;
    private String showName;
    private LocalDateTime showDateTime;
    private Integer durationMinutes;

    private Long eventId;
    private Long auditoriumId;

    private Set<Genre> genres;
    private Set<Language> languages;
}
