package com.thiru.BookMyShow.ShowMgmt.show.DTO;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

import com.thiru.BookMyShow.ShowMgmt.event.Genre;
import com.thiru.BookMyShow.ShowMgmt.show.*;

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
