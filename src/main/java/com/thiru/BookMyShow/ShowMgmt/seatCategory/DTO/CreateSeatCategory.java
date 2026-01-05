package com.thiru.BookMyShow.ShowMgmt.seatCategory.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSeatCategory {

    @NotBlank(message = "Category name is required")
    @Size(max = 50)
    private String name;

    private String userName;

    @Size(max = 255)
    private String description;
}
