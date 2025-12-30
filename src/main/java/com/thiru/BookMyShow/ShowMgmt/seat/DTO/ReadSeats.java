package com.thiru.BookMyShow.ShowMgmt.seat.DTO;

import lombok.*;

@Getter
@Setter
public class ReadSeats {
    private Long auditoriumId;
    private ReadSeat[] seats;
}
