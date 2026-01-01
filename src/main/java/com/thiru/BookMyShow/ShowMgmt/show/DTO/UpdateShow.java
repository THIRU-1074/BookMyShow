package com.thiru.BookMyShow.ShowMgmt.show.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

import com.thiru.BookMyShow.ShowMgmt.show.*;

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

    private Long BookedSeatCount;

    private ShowBookingStatus bookingStatus;
}
