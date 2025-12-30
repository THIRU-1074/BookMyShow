package com.thiru.BookMyShow.ShowMgmt.showSeat.DTO;

import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.BookMyShow.ShowMgmt.showSeat.*;

import lombok.*;

@Getter
@Builder
public class ShowSeatReadResponse {

    private Long showSeatId;
    private Long seatId;
    private String seatNo;

    private SeatCategoryEntity category;
    private SeatAvailabilityStatus status;
}
