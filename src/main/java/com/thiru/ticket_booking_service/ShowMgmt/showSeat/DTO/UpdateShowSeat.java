package com.thiru.ticket_booking_service.ShowMgmt.showSeat.DTO;

import lombok.*;

import com.thiru.ticket_booking_service.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.ticket_booking_service.ShowMgmt.showSeat.*;

import jakarta.validation.constraints.NotNull;

@Setter
@Getter
public class UpdateShowSeat {
    @NotNull(message = "Seat Id is required ...!")
    private Long seatId;

    private SeatCategoryEntity category;

    private SeatAvailabilityStatus status;
}
