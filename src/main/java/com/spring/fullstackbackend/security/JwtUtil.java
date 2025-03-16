package com.spring.fullstackbackend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Auto-generated key

    public static String getSecretKey() {
        return Base64.getEncoder().encodeToString(SECRET_KEY.getEncoded()); // Convert to Base64
    }

    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15 minutes
    private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24; // 24 hours

    public String generateAccessToken(String username, List<String> roles) {
        return createToken(username, roles, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(String username) {
        return createToken(username, List.of(), REFRESH_TOKEN_EXPIRATION);
    }

    private String createToken(String username, List<String> roles, long expiration) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles) // Add roles to JWT claims
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class); // Retrieve roles from JWT
    }

    public boolean isTokenValid(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
