package com.c107.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    // JWT 서명에 사용할 Key
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // AccessToken 생성 시
    public String generateAccessToken(String role, String email, String nickname) {
        return generateToken(role, email, nickname, accessTokenExpiration);
    }

    // RefreshToken 생성 시
    public String generateRefreshToken(String role, String email, String nickname) {
        return generateToken(role, email, nickname, refreshTokenExpiration);
    }

    private String generateToken(String role, String email, String nickname, long expiration) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)         // 문자열 role
                .claim("nickname", nickname) // 닉네임 claim
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 검증
    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
