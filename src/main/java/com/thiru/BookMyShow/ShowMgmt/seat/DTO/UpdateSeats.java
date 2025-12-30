package com.thiru.BookMyShow.ShowMgmt.seat.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSeats {
    @NotBlank(message = "user name is required ...!")
    private String userName;

    @NotNull(message = "auditoriumId is required")
    private Long auditoriumId;

    @NotNull(message = "seats array is required")
    @Size(min = 1, message = "At least one seat must be provided")
    private UpdateSeat[] seats;
}
