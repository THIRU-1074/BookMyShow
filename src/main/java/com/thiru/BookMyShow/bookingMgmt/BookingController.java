package com.thiru.BookMyShow.bookingMgmt;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.*;
import jakarta.validation.*;
import java.util.*;
import com.thiru.BookMyShow.bookingMgmt.DTO.*;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/createBookings")
    public ResponseEntity<Long> createBooking(
            Authentication authentication,
            @RequestBody @Valid CreateBookings request) {
        Claims claims = (Claims) authentication.getPrincipal();
        request.setUserName(claims.getSubject());
        bookingService.createBookings(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
    // bookingService.deleteBooking(id);
    // return ResponseEntity.noContent().build();
    // }

    @DeleteMapping("/deleteBookings")
    public ResponseEntity<Long> deleteBookings(Authentication authentication,
            @RequestBody @Valid DeleteBookings request) {
        Claims claims = (Claims) authentication.getPrincipal();
        request.setUserName(claims.getSubject());
        bookingService.deleteBookings(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/readBookings")
    public ResponseEntity<?> readBooking(
            Authentication authentication,
            @ModelAttribute @Valid ReadBookings request) {
        Claims claims = (Claims) authentication.getPrincipal();
        request.setUserName(claims.getSubject());
        List<ReadBookingResponse> bookings = bookingService.readBookings(request);
        return ResponseEntity.ok(bookings);
    }
}
