package com.thiru.BookMyShow.appSecurity;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.thiru.BookMyShow.userMgmt.UserEntity;
import com.thiru.BookMyShow.userMgmt.UserLoginRequestDTO;

import java.util.Date;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoderDTO passwordEncoderService;
    private final JwtPropertiesDTO jwtProps;

    /* ---------------- PASSWORD ---------------- */

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoderService.matches(rawPassword, hashedPassword);
    }

    /* ---------------- JWT ---------------- */

    public String generateRefreshToken(UserEntity user) {
        return Jwts.builder()
                .setSubject(user.getName())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProps.getRefreshTokenValidityMs()))
                .signWith(jwtProps.getKey(), SignatureAlgorithm.HS256)
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

    public String generateAccessToken(String token) {
        Claims claims = extractClaims(token);

        return Jwts.builder()
                .setSubject(claims.getSubject())
                .claim("role", claims.get("role"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProps.getAccessTokenValidityMs()))
                .signWith(jwtProps.getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public void resolveAuthHeader(String authHeader, UserLoginRequestDTO request) {
        AuthType authType = null;
        if (authHeader == null || authHeader.isBlank()) {
            throw new IllegalArgumentException("Authorization header missing");
        } else if (authHeader.startsWith("Bearer")) {
            authType = AuthType.JWT;
            request.setPassword(authHeader.substring(7));
        } else if (authHeader.startsWith("Basic")) {
            authType = AuthType.BASIC;
            String basicToken = new String(
                    Base64.getDecoder().decode(authHeader.substring(6)),
                    StandardCharsets.UTF_8);

            request.setUserName(basicToken.split(":")[0]);
            request.setPassword(basicToken.split(":")[1]);
        } else
            throw new IllegalArgumentException("Unsupported Authorization type");
        request.setAuthType(authType);
    }
    /* -------- NON-LOGIN AUTH (TOKEN VERIFY) -------- */

    public Claims verifyAccessToken(String token) {
        return extractClaims(token); // throws exception if invalid/expired
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtProps.getKey())
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
