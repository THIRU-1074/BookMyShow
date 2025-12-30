package com.thiru.ticket_booking_service.ShowMgmt.venue;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.*;
import java.util.*;

import com.thiru.ticket_booking_service.ShowMgmt.venue.DTO.*;

@RestController
@RequestMapping("/venue")
@RequiredArgsConstructor
public class VenueController {
    private final VenueService venueService;

    @PostMapping("/createVenue")
    public ResponseEntity<?> createVenue(
            @RequestBody CreateVenue venueDTO,
            Authentication authentication) {

        Claims claims = (Claims) authentication.getPrincipal();
        venueDTO.setUserName(claims.getSubject());
        venueService.create(venueDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/readvenue")
    public ResponseEntity<?> readvenue(
            @RequestBody ReadVenue venueDTO,
            Authentication authentication) {

        List<VenueReadResponse> venues = venueService.read(venueDTO);

        return ResponseEntity
                .ok(venues);
    }
}
