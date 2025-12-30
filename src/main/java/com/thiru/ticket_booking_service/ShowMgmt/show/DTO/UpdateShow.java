package com.thiru.ticket_booking_service.ShowMgmt.show.DTO;

import com.thiru.ticket_booking_service.ShowMgmt.event.Genre;
import com.thiru.ticket_booking_service.ShowMgmt.show.*;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class UpdateShow {

    private Long eventId;

    private String showName;

    private LocalDateTime showDateTime;

    private Integer durationMinutes;

    private Long auditoriumId;

    private Set<Genre> genres;

    private Set<Language> languages;

    @NotBlank(message = "User name is required...!")
    private String userName;

    private Long showId;
}
