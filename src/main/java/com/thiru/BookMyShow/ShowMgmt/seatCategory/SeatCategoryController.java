package com.thiru.BookMyShow.ShowMgmt.seatCategory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

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

    @PostMapping("/createSeatCategory")
    public ResponseEntity<SeatCategoryResponse> create(Authentication authentication,
            @Valid @RequestBody CreateSeatCategory request) {
        Claims claims = (Claims) authentication.getPrincipal();
        request.setUserName(claims.getSubject());
        SeatCategoryResponse response = seatCategoryService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/readSeatCategory")
    public ResponseEntity<?> read(Authentication authentication,
            @ModelAttribute ReadSeatCategory request) {
        List<SeatCategoryResponse> response = seatCategoryService.read(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
