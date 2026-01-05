package com.thiru.BookMyShow.ShowMgmt.showSeat.DTO;

import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShowSeats {
    private String userName;

    @NotNull(message = "showId is required")
    private Long showId;

    @NotNull(message = "seatIds are required")
    @Size(min = 1, message = "At least one update must be provided")
    private List<UpdateShowSeat> seats;

}
