package com.thiru.BookMyShow.ShowMgmt.showSeat.DTO;

import lombok.*;

import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.BookMyShow.ShowMgmt.showSeat.*;

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
