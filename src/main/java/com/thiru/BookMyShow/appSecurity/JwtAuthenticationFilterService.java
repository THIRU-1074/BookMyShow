package com.thiru.BookMyShow.appSecurity;

import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilterService extends OncePerRequestFilter {

    private final AuthService authService;
    private static final AntPathMatcher matcher = new AntPathMatcher();

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/users/login",
            "/users/signup",
            "/swagger-ui/**",
            "/v3/api-docs/**");

    public JwtAuthenticationFilterService(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No token → let Spring decide (will become 401 if endpoint is protected)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = authService.verifyAccessToken(token);

            // Create Authentication object
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    claims, // usually userId or email
                    null,
                    List.of() // roles if you have them
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
                        {
                          "error": "Invalid or expired access token"
                        }
                    """);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(p -> matcher.match(p, request.getRequestURI()));
    }
}
