package com.thiru.BookMyShow.bookingMgmt.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
public class CreateBooking {

    @NotNull
    private Long seatId;

    @NotNull
    private Long showId;
}
