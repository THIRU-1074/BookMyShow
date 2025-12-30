package com.thiru.ticket_booking_service.ShowMgmt.show.DTO;

import com.thiru.ticket_booking_service.ShowMgmt.event.Genre;
import com.thiru.ticket_booking_service.ShowMgmt.show.*;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class ReadShow {

    private Long eventId;

    private String showName;

    private LocalDateTime showDateTime;

    private LocalDateTime fromDateTime;
    private LocalDateTime toDateTime;

    private Integer durationMinutes;

    private Long auditoriumId;

    private Set<Genre> genres;

    private Set<Language> languages;

    private String userName;

    private Long showId;
}
