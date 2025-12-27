package com.thiru.ticket_booking_service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserBookingSummary {

    private Long bookingId;
    private Long eventId;
    private Long showId;
    private String seatNo;
    private String seatCategory;
}
