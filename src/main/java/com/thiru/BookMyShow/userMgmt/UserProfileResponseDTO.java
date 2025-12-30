package com.thiru.BookMyShow.userMgmt;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class UserProfileResponseDTO {

    private Long userId;
    private String name;
    private String mailId;
    private String phoneNumber;
    private String role;
    private List<UserBookingSummaryDTO> bookings;
}
