package com.thiru.ticket_booking_service.ShowMgmt.seat.DTO;

import lombok.*;

@Getter
@Setter
public class ReadSeats {
    private Long auditoriumId;
    private ReadSeat[] seats;
}
