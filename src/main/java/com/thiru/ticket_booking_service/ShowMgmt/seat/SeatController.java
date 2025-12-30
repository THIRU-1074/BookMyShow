package com.thiru.ticket_booking_service.ShowMgmt.seat;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.*;
import java.util.*;

import com.thiru.ticket_booking_service.ShowMgmt.seat.DTO.*;

@RestController
@RequestMapping("/seat")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @PostMapping("/createseat")
    public ResponseEntity<?> createseat(
            @RequestBody CreateSeats seatDTO,
            Authentication authentication) {

        Claims claims = (Claims) authentication.getPrincipal();
        seatDTO.setUserName(claims.getSubject());
        seatService.create(seatDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED).body(null);
    }

    @PatchMapping("/updateSeat")
    public ResponseEntity<?> updateseat(
            @RequestBody UpdateSeats seatDTO,
            Authentication authentication) {

        Claims claims = (Claims) authentication.getPrincipal();
        seatDTO.setUserName(claims.getSubject());
        seatService.update(seatDTO);

        return ResponseEntity
                .noContent().build();
    }

    @GetMapping("/readseat")
    public ResponseEntity<?> readseat(
            @RequestBody ReadSeats seatDTO,
            Authentication authentication) {

        List<SeatReadResponse> seats = seatService.read(seatDTO);

        return ResponseEntity
                .ok(seats);
    }
}
