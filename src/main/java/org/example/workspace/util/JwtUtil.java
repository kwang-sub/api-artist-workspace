package org.example.workspace.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.workspace.common.ApplicationConstant;
import org.example.workspace.dto.response.AuthTokenResDto;
import org.example.workspace.entity.code.RoleType;
import org.example.workspace.security.CustomUserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class JwtUtil {
    // TODO 토큰 정보 설정 필요

    //    @Value("${jwt.secret}")
    private String secret = "testdsgafdsghbnfaidbnmoiterjpnhqihtnjpiunsdafgpiuafndhgipuadnfghiuapnfbinrtghqeiouhniadsgfadfbtqh";

    //    @Value("${jwt.expiration}")
    private long jwtExpirationMs = 50000;

    //    @Value("${jwt.refreshExpiration}")
    private long refreshExpirationMs = 60000;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public AuthTokenResDto generateToken(CustomUserDetails userDetails) {
        String accessToken = this.generateAccessToken(userDetails);
        String refreshToken = this.generateRefreshToken(userDetails);
        return new AuthTokenResDto(accessToken, refreshToken);
    }

    private String generateAccessToken(CustomUserDetails userDetails) {
        RoleType roleType = userDetails.getRoleType();
        return generateToken(userDetails.getUsername(), roleType, jwtExpirationMs);
    }

    private String generateRefreshToken(CustomUserDetails userDetails) {
        RoleType roleType = userDetails.getRoleType();
        return generateToken(userDetails.getUsername(), roleType, refreshExpirationMs);
    }

    private String generateToken(String username, RoleType roleType, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ApplicationConstant.Jwt.CLAIMS_KEY_ROLE, roleType.name());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // 토큰에서 username 추출
    public String extractUsername(String token) {
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

    // 토큰 만료 여부 확인
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    // 토큰 검증
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }


}
