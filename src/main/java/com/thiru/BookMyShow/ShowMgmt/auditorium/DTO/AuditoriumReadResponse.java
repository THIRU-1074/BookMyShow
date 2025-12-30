package com.thiru.BookMyShow.ShowMgmt.auditorium.DTO;

import lombok.*;

@Getter
@Setter
@Builder
public class AuditoriumReadResponse {
    private Long auditoriumId;
    private String auditoriumName;
    private Long venueId;
}
