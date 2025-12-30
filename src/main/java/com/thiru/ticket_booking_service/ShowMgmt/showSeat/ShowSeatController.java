package com.thiru.ticket_booking_service.ShowMgmt.showSeat;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.*;
import java.util.*;

import com.thiru.ticket_booking_service.ShowMgmt.showSeat.DTO.*;

@RestController
@RequestMapping("/showSeat")
@RequiredArgsConstructor
public class ShowSeatController {
    private final ShowSeatService showSeatService;

    @PatchMapping("/updateShowSeat")
    public ResponseEntity<?> updateShowSeat(
            @RequestBody UpdateShowSeats showSeatDTO,
            Authentication authentication) {

        Claims claims = (Claims) authentication.getPrincipal();
        showSeatDTO.setUserName(claims.getSubject());
        showSeatService.update(showSeatDTO);

        return ResponseEntity
                .noContent().build();
    }

    @GetMapping("/readShowSeat")
    public ResponseEntity<?> readshowSeat(
            @RequestBody ReadShowSeat showSeatDTO,
            Authentication authentication) {

        List<ShowSeatReadResponse> showSeats = showSeatService.read(showSeatDTO);

        return ResponseEntity
                .ok(showSeats);
    }
}
