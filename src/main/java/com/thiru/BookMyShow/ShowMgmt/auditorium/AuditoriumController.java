package com.thiru.BookMyShow.ShowMgmt.auditorium;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.thiru.BookMyShow.ShowMgmt.auditorium.DTO.*;

import org.springframework.http.*;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.*;
import java.util.*;

@RestController
@RequestMapping("/auditorium")
@RequiredArgsConstructor
public class AuditoriumController {
        private final AuditoriumService auditoriumService;

        @PostMapping("/createAuditorium")
        public ResponseEntity<?> createAuditorium(
                        @RequestBody createAuditorium auditoriumDTO,
                        Authentication authentication) {

                Claims claims = (Claims) authentication.getPrincipal();
                auditoriumDTO.setUserName(claims.getSubject());
                auditoriumService.create(auditoriumDTO);

                return ResponseEntity
                                .status(HttpStatus.CREATED).body(null);
        }

        @PatchMapping("/updateAuditorium")
        public ResponseEntity<?> updateAuditorium(
                        @RequestBody updateAuditorium auditoriumDTO,
                        Authentication authentication) {

                Claims claims = (Claims) authentication.getPrincipal();
                auditoriumDTO.setUserName(claims.getSubject());
                auditoriumService.update(auditoriumDTO);

                return ResponseEntity
                                .noContent().build();
        }

        @DeleteMapping("/deleteAuditorium")
        public ResponseEntity<?> deleteAuditorium(
                        @RequestBody deleteAuditorium auditoriumDTO,
                        Authentication authentication) {

                Claims claims = (Claims) authentication.getPrincipal();
                auditoriumDTO.setUserName(claims.getSubject());
                auditoriumService.delete(auditoriumDTO);

                return ResponseEntity

                                .noContent().build();
        }

        @GetMapping("/readAuditorium")
        public ResponseEntity<?> readAuditorium(
                        @ModelAttribute readAuditorium auditoriumDTO,
                        Authentication authentication) {

                Claims claims = (Claims) authentication.getPrincipal();
                auditoriumDTO.setUserName(claims.getSubject());
                List<AuditoriumReadResponse> auditoriums = auditoriumService.read(auditoriumDTO);
                return ResponseEntity
                                .ok(auditoriums);
        }
}
