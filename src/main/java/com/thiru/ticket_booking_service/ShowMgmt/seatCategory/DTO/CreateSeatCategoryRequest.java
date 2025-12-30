package com.thiru.ticket_booking_service.ShowMgmt.seatCategory.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSeatCategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 50)
    private String name;

    @Size(max = 255)
    private String description;
}
