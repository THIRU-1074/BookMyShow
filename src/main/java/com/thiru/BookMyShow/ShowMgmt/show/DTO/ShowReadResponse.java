package com.thiru.BookMyShow.ShowMgmt.show.DTO;

import lombok.*;
import java.util.*;

import com.thiru.BookMyShow.ShowMgmt.show.*;

import java.time.LocalDateTime;

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

    private Long availableSeatCount;
}
