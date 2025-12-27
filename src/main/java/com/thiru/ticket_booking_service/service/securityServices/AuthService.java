package com.thiru.ticket_booking_service.service.securityServices;

import com.thiru.ticket_booking_service.entity.*;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoderService;

    private static final long ACCESS_TOKEN_VALIDITY_MS = 15 * 60 * 1000; // 15 mins
    private static final String SECRET = "VERY_SECRET_KEY_CHANGE_LATER_123456789";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    /* ---------------- PASSWORD ---------------- */

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoderService.matches(rawPassword, hashedPassword);
    }

    /* ---------------- JWT ---------------- */

    public String generateAccessToken(UserEntity user) {
        return Jwts.builder()
                .setSubject(user.getMailId())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String refreshAccessToken(String token) {
        Claims claims = extractClaims(token);

        return Jwts.builder()
                .setSubject(claims.getSubject())
                .claim("role", claims.get("role"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /* -------- NON-LOGIN AUTH (TOKEN VERIFY) -------- */

    public Claims verifyAccessToken(String token) {
        return extractClaims(token); // throws exception if invalid/expired
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String hash(String rawPassword) {
        return passwordEncoderService.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String hashedPassword) {
        return passwordEncoderService.matches(rawPassword, hashedPassword);
    }
}

