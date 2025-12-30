package com.thiru.ticket_booking_service.ShowMgmt.showSeat.DTO;

import lombok.*;

import com.thiru.ticket_booking_service.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.ticket_booking_service.ShowMgmt.showSeat.*;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class ReadShowSeat {

    @NotNull(message = "showId is required")
    private Long showId;

    private Long seatId;
    private SeatCategoryEntity category;
    private SeatAvailabilityStatus status;
}
