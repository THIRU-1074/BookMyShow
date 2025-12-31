package com.thiru.BookMyShow.bookingMgmt.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteBookings {
    @NotBlank(message = "User Id required..!")
    String userName;

    @Size(min = 1)
    Long[] bookingIds;
}
