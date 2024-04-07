package com.example.factory.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.security.key}")
    private String jwtSecret;

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(expiredTime())
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public void deactivateToken(String token) {
        getClaims(token).setExpiration(new Date());
    }

    private Date expiredTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime midday = localDateTime.withHour(12).withMinute(0).withSecond(0).withNano(0);
        if (localDateTime.isBefore(midday)) {
            return Date.from(midday.atZone(ZoneId.systemDefault()).toInstant());
        } else {
            return Date.from(localDateTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0).atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(authToken);
            System.out.println(getClaims(authToken).getExpiration());
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println(e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println(e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println(e.getMessage());
        } catch (SignatureException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey()).build()
                .parseClaimsJws(token)
                .getBody();
    }
}
