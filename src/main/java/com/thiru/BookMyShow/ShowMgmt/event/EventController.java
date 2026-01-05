package com.thiru.BookMyShow.ShowMgmt.event;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.*;
import java.util.*;

import com.thiru.BookMyShow.ShowMgmt.event.DTO.*;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
        private final EventService eventService;

        @PostMapping("/createEvent")
        public ResponseEntity<?> createEvent(
                        @RequestBody CreateEvent eventDTO,
                        Authentication authentication) {

                Claims claims = (Claims) authentication.getPrincipal();
                eventDTO.setUserName(claims.getSubject());
                eventService.create(eventDTO);

                return ResponseEntity
                                .status(HttpStatus.CREATED).body(null);
        }

        @PatchMapping("/updateEvent")
        public ResponseEntity<?> updateevent(
                        @RequestBody UpdateEvent eventDTO,
                        Authentication authentication) {

                Claims claims = (Claims) authentication.getPrincipal();
                eventDTO.setUserName(claims.getSubject());
                eventService.update(eventDTO);

                return ResponseEntity
                                .noContent().build();
        }

        @DeleteMapping("/deleteEvent")
        public ResponseEntity<?> deleteevent(
                        @RequestBody DeleteEvent eventDTO,
                        Authentication authentication) {

                Claims claims = (Claims) authentication.getPrincipal();
                eventDTO.setUserName(claims.getSubject());
                eventService.delete(eventDTO);

                return ResponseEntity
                                .noContent().build();
        }

        @GetMapping("/readEvent")
        public ResponseEntity<?> readevent(
                        @ModelAttribute ReadEvent eventDTO,
                        Authentication authentication) {

                Claims claims = (Claims) authentication.getPrincipal();
                eventDTO.setUserName(claims.getSubject());
                List<EventReadResponse> events = eventService.read(eventDTO);

                return ResponseEntity
                                .ok(events);
        }
}
