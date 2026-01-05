package com.thiru.BookMyShow.ShowMgmt.seatCategory.DTO;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadSeatCategory {
    private Long id;

    @Size(max = 50)
    private String name;
}
