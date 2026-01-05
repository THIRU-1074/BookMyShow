package com.thiru.BookMyShow.ShowMgmt.seat.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
public class ReadSeats {
    @NotNull(message = "auditoriumId is required")
    private Long auditoriumId;

    private ReadSeat[] seats;
}
