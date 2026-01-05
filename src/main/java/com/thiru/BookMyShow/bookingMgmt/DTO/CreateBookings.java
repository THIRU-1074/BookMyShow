package com.thiru.BookMyShow.bookingMgmt.DTO;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.*;

@Getter
@Setter
public class CreateBookings {
    private String userName;

    @NotEmpty
    private List<CreateBooking> bookings;
}
