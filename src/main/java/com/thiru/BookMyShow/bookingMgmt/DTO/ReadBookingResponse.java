package com.thiru.BookMyShow.bookingMgmt.DTO;

import java.time.LocalDateTime;

import com.thiru.BookMyShow.ShowMgmt.seat.DTO.SeatReadResponse;
import com.thiru.BookMyShow.ShowMgmt.venue.DTO.VenueReadResponse;

import lombok.*;

@Getter
@Setter
@Builder
public class ReadBookingResponse {
    Long bookingId;
    LocalDateTime bookingTime;

    Long eventId;
    String eventName;

    VenueReadResponse venue;

    Long auditoriumId;
    String auditoriumName;

    SeatReadResponse seat;
    String seatCategory;
    Double price;

    Long showId;
    String showName;
    LocalDateTime showDateTime;
    Integer duration;
}
