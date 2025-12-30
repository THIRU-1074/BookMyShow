package com.thiru.ticket_booking_service.ShowMgmt.auditorium.DTO;

import lombok.*;

@Getter
@Setter
@Builder
public class AuditoriumReadResponse {
    private Long auditoriumId;
    private String auditoriumName;
    private Long venueId;
}
