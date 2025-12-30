package com.thiru.ticket_booking_service.ShowMgmt.seatCategory.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatCategoryResponse {

    private Long id;
    private String name;
    private String description;
}
