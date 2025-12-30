package com.thiru.BookMyShow.ShowMgmt.seat.DTO;

import lombok.*;

@Setter
@Getter
@Builder
public class SeatReadResponse {

    private Long seatId;
    private String seatNo;
    private Integer row;
    private Integer col;
    private Integer stance;
}
