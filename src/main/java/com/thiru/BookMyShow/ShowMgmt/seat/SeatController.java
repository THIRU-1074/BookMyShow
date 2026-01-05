package com.thiru.BookMyShow.ShowMgmt.seat;

import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import lombok.RequiredArgsConstructor;
import java.util.*;

import com.thiru.BookMyShow.ShowMgmt.seat.DTO.*;

@RestController
@RequestMapping("/seat")
@RequiredArgsConstructor
public class SeatController {
        private final SeatService seatService;

        @PostMapping("/createSeat")
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

        @GetMapping("/readSeat")
        public ResponseEntity<?> readseat(
                        @ModelAttribute ReadSeats seatDTO,
                        Authentication authentication) {

                List<SeatReadResponse> seats = seatService.read(seatDTO);

                return ResponseEntity
                                .ok(seats);
        }
}
