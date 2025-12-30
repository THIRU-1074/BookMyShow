package com.thiru.BookMyShow.ShowMgmt.seatCategory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thiru.BookMyShow.ShowMgmt.seatCategory.DTO.*;

@RestController
@RequestMapping("/api/seat-categories")
@RequiredArgsConstructor
public class SeatCategoryController {

    private final SeatCategoryService seatCategoryService;

    @PostMapping
    public ResponseEntity<SeatCategoryResponse> create(
            @Valid @RequestBody CreateSeatCategoryRequest request) {

        SeatCategoryResponse response = seatCategoryService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
