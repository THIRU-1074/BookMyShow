package com.thiru.BookMyShow.bookingMgmt.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReadBookings {
    String userName;

    Long showId;

    Long seatCategoryId;

    Long[] bookingIds;
}
