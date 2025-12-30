package com.thiru.ticket_booking_service.ShowMgmt.showSeat.DTO;

import lombok.*;

import com.thiru.ticket_booking_service.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.ticket_booking_service.ShowMgmt.showSeat.*;

@Getter
@Builder
public class ShowSeatReadResponse {

    private Long showSeatId;
    private Long seatId;
    private String seatNo;

    private SeatCategoryEntity category;
    private SeatAvailabilityStatus status;
}
