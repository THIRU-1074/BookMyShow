package com.thiru.BookMyShow.ShowMgmt.showSeatPricing.DTO;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class CreateShowSeatPricing {

    @NotNull
    private Long showId;

    @NotBlank
    private String seatCategoryName;

    @NotNull
    @Positive
    private Double price;

    private String userName;
}
