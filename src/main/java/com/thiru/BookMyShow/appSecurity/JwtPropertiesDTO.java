package com.thiru.BookMyShow.appSecurity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import io.jsonwebtoken.security.Keys;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.security.jwt")
public class JwtPropertiesDTO {

    private long accessTokenValidityMinutes;
    private long refreshTokenValidityDays;

    private long accessTokenValidityMs;
    private long refreshTokenValidityMs;

    private String secret;
    private Key key;

    @PostConstruct
    public void init() {
        if (secret == null) {
            throw new IllegalStateException(
                    "JWT secret must be at least 32 characters long");
        }

        if (accessTokenValidityMinutes <= 0 || refreshTokenValidityDays <= 0) {
            throw new IllegalStateException(
                    "JWT validity values must be > 0");
        }
        this.accessTokenValidityMs = TimeUnit.MINUTES.toMillis(accessTokenValidityMinutes);

        this.refreshTokenValidityMs = TimeUnit.DAYS.toMillis(refreshTokenValidityDays);
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }
}
