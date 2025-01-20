package org.example.workspace.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.example.workspace.common.ApplicationConstant;
import org.example.workspace.dto.response.AuthTokenResDto;
import org.example.workspace.entity.code.RoleType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final Clock clock;

    @Value("${jwt.secret}")
    private String secret;

    private final long accessTokenExpirationMs = 5 * 60 * 1000;

    private final long refreshTokenExpirationMs = 24 * 60 * 60 * 1000;
    private final long emailVerifyTokenExpirationMs = 60 * 60 * 1000;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public AuthTokenResDto generateSignInToken(String username, RoleType roleType) {
        String accessToken = this.generateAccessToken(username, roleType);
        String refreshToken = this.generateRefreshToken(username, roleType);
        return new AuthTokenResDto(accessToken, refreshToken);
    }

    private String generateAccessToken(String username, RoleType roleType) {
        return generateSignInToken(username, roleType, accessTokenExpirationMs);
    }

    private String generateRefreshToken(String username, RoleType roleType) {
        return generateSignInToken(username, roleType, refreshTokenExpirationMs);
    }

    private String generateSignInToken(String username, RoleType roleType, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ApplicationConstant.Jwt.CLAIMS_KEY_ROLE, roleType.name());
        return generateToken(username, expiration, claims);
    }

    public String generateEmailVerifyToken(String email, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ApplicationConstant.Jwt.CLAIMS_KEY_ID, userId);
        return generateToken(email, emailVerifyTokenExpirationMs, claims);
    }

    private String generateToken(String subject, long expiration, Map<String, Object> claims) {
        Instant now = Instant.now(clock);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expiration)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public RoleType extractRole(String token) {
        String roleTypeString = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(ApplicationConstant.Jwt.CLAIMS_KEY_ROLE, String.class);

        return RoleType.valueOf(roleTypeString);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(Date.from(Instant.now(clock)));
    }

    private Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractSubject(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
