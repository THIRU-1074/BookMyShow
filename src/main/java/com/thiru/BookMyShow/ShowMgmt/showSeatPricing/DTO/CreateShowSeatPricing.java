package com.thiru.BookMyShow.ShowMgmt.showSeatPricing.DTO;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class CreateShowSeatPricing {

    @NotNull
    private Long showId;

    @NotNull
    private Long seatCategoryId;

    @NotNull
    @Positive
    private Double price;

    @NotBlank(message = "user Name required...!")
    private String userName;
}
