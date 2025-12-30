package com.thiru.ticket_booking_service.ShowMgmt.auditorium.DTO;

import lombok.*;

@Getter
@Setter
public class readAuditorium {
    // Read by userName
    private String userName;

    // Read by venue id
    private Long venueId;
}
