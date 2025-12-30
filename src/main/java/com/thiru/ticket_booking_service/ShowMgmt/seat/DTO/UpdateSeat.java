package com.thiru.ticket_booking_service.ShowMgmt.seat.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSeat {
    @NotBlank(message = "Seat id required ...!")
    private Long seatId;

    private String seatNo; // A1, A2, B3 etc
}
