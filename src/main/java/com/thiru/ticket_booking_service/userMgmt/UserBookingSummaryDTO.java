package com.thiru.ticket_booking_service.userMgmt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserBookingSummaryDTO {

    private Long bookingId;
    private Long eventId;
    private Long showId;
    private String seatNo;
    private String seatCategory;
}
