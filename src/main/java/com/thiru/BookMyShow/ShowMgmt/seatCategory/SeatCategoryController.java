package com.thiru.BookMyShow.ShowMgmt.seatCategory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;

import com.thiru.BookMyShow.ShowMgmt.seatCategory.DTO.*;

@RestController
@RequestMapping("/seatCategory")
@RequiredArgsConstructor
public class SeatCategoryController {

    private final SeatCategoryService seatCategoryService;

    @PostMapping("/createCategory")
    public ResponseEntity<SeatCategoryResponse> create(Authentication authentication,
            @Valid @RequestBody CreateSeatCategoryRequest request) {
        Claims claims = (Claims) authentication.getPrincipal();
        request.setUserName(claims.getSubject());
        SeatCategoryResponse response = seatCategoryService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
