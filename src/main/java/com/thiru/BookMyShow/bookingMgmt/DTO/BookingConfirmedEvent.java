package com.thiru.BookMyShow.bookingMgmt.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookingConfirmedEvent {

    private Long bookingId;
    private String userName;
    private String email;
    private List<String> seatNumbers;
    private Double amount;
}
