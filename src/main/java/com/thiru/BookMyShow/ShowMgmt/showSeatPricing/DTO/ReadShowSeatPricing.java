package com.thiru.BookMyShow.ShowMgmt.showSeatPricing.DTO;

import lombok.*;

@Getter
@Setter
public class ReadShowSeatPricing {
    private Long id;
    private Long showId;
    private String seatCategoryName;
}
