package com.thiru.BookMyShow.userMgmt.DTO;

import lombok.Builder;
import lombok.Getter;
import java.util.List;
import com.thiru.BookMyShow.bookingMgmt.DTO.*;
import com.thiru.BookMyShow.userMgmt.*;

@Getter
@Builder
public class UserProfileResponseDTO {

    private Long userId;
    private String name;
    private String mailId;
    private String phoneNumber;
    private Role role;
    private List<ReadBookingResponse> bookings;
}
