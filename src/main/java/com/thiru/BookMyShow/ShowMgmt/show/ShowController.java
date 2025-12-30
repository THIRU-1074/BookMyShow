package com.thiru.BookMyShow.ShowMgmt.show;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.thiru.BookMyShow.ShowMgmt.show.DTO.*;

import org.springframework.http.*;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.*;
import java.util.*;

@RestController
@RequestMapping("/show")
@RequiredArgsConstructor
public class ShowController {
        private final ShowService showService;

        @PostMapping("/createShow")
        public ResponseEntity<?> createshow(
                        @RequestBody CreateShow showDTO,
                        Authentication authentication) {

                Claims claims = (Claims) authentication.getPrincipal();
                showDTO.setUserName(claims.getSubject());
                showService.create(showDTO);

                return ResponseEntity
                                .status(HttpStatus.CREATED).body(null);
        }

        @PatchMapping("/updateShow")
        public ResponseEntity<?> updateShow(
                        @RequestBody UpdateShow showDTO,
                        Authentication authentication) {

                Claims claims = (Claims) authentication.getPrincipal();
                showDTO.setUserName(claims.getSubject());
                showService.update(showDTO);

                return ResponseEntity
                                .noContent().build();
        }

        @DeleteMapping("/deleteShow")
        public ResponseEntity<?> deleteshow(
                        @RequestBody DeleteShow showDTO,
                        Authentication authentication) {

                Claims claims = (Claims) authentication.getPrincipal();
                showDTO.setUserName(claims.getSubject());
                showService.delete(showDTO);

                return ResponseEntity
                                .noContent().build();
        }

        @GetMapping("/readShow")
        public ResponseEntity<?> readshow(
                        @RequestBody ReadShow showDTO,
                        Authentication authentication) {

                Claims claims = (Claims) authentication.getPrincipal();
                showDTO.setUserName(claims.getSubject());
                List<ShowReadResponse> shows = showService.read(showDTO);

                return ResponseEntity
                                .ok(shows);
        }
}
