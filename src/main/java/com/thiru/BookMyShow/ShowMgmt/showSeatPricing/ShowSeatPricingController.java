package com.thiru.BookMyShow.ShowMgmt.showSeatPricing;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.*;
import jakarta.validation.*;

import com.thiru.BookMyShow.ShowMgmt.showSeatPricing.DTO.*;

@RestController
@RequestMapping("/showSeatPricing")
@RequiredArgsConstructor
public class ShowSeatPricingController {

    private final ShowSeatPricingService pricingService;

    @PostMapping("/createShowSeatPricing")
    public ResponseEntity<Long> createPricing(
            @Valid @RequestBody CreateShowSeatPricing request, Authentication authentication) {
        Claims claims = (Claims) authentication.getPrincipal();
        request.setUserName(claims.getSubject());
        Long id = pricingService.createPricing(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @DeleteMapping("/deleteShowSeatPricing/{id}")
    public ResponseEntity<Void> deletePricing(
            @PathVariable Long id, Authentication authentication) {
        Claims claims = (Claims) authentication.getPrincipal();
        pricingService.deletePricing(id, claims.getSubject());
        return ResponseEntity.noContent().build();
    }
}
