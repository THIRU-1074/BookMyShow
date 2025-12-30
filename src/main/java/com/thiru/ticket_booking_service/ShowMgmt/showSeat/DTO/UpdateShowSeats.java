package com.thiru.ticket_booking_service.ShowMgmt.showSeat.DTO;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.*;

@Getter
@Setter
@Builder
public class UpdateShowSeats {
    @NotBlank(message = "user name required...!")
    private String userName;

    @NotNull(message = "showId is required")
    private Long showId;

    @NotNull(message = "seatIds are required")
    @Size(min = 1, message = "At least one update must be provided")
    private List<UpdateShowSeat> seats;

}
