package com.thiru.BookMyShow.ShowMgmt.seat.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
public class CreateSeat {

    @NotNull(message = "row is required")
    private Integer row;

    @NotNull(message = "col is required")
    private Integer col;

    @NotNull(message = "Required Stance or Section number...!")
    private Integer stance;

    private String seatNo;
}
