package com.thiru.ticket_booking_service.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class UserProfileResponse {

    private Long userId;
    private String name;
    private String mailId;
    private String phoneNumber;
    private String role;
    private List<UserBookingSummary> bookings;
}

