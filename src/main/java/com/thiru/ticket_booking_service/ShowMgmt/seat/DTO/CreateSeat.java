package com.thiru.ticket_booking_service.ShowMgmt.seat.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
public class CreateSeat {

    @NotNull(message = "row is required")
    private Integer row;

    @NotNull(message = "col is required")
    private Integer col;

    private Integer stance;

    private String seatNo;
}
