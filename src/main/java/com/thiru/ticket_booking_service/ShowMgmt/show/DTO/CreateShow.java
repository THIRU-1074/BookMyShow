package com.thiru.ticket_booking_service.ShowMgmt.show.DTO;

import com.thiru.ticket_booking_service.ShowMgmt.event.Genre;
import com.thiru.ticket_booking_service.ShowMgmt.show.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class CreateShow {

    @NotNull(message = "eventId is required")
    private Long eventId;

    @NotBlank(message = "showName is required")
    private String showName;

    @NotNull(message = "showTime is required")
    private LocalDateTime showDateTime;

    @NotNull(message = "durationMinutes is required")
    private Integer durationMinutes;

    @NotNull(message = "auditoriumId is required")
    private Long auditoriumId;

    private Set<Genre> genres;

    private Set<Language> languages;

    private String userName;
}
