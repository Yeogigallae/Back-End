package com.umc.yeogi_gal_lae.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    public String createAccessToken(String email) {
        return generateToken(email, accessTokenValidity);
    }

    public String createRefreshToken(String email) {
        return generateToken(email, refreshTokenValidity);
    }

    private Key getSigningKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            // Base64 인코딩 오류 시 일반 문자열로 처리
            return Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }
    }

    private String generateToken(String email, long validity) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validity);
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public int getAccessTokenValidity() {
        return (int) accessTokenValidity;  // 명시적 형변환 추가
    }

    public int getRefreshTokenValidity() {
        return (int) refreshTokenValidity;  // 명시적 형변환 추가
    }
}
