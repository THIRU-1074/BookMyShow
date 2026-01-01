package com.thiru.BookMyShow.ShowMgmt.show.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

import com.thiru.BookMyShow.ShowMgmt.show.*;

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
