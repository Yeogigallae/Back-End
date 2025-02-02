package com.umc.yeogi_gal_lae.global.jwt.service;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.global.jwt.JwtToken;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
@Getter
@Slf4j
public class JwtService {

    private static final String BEARER_PREFIX = "Bearer ";

    private final Key secretKey;
    private final long accessTokenValidTime;   // ms
    private final long refreshTokenValidTime;  // ms

    @Value("${jwt.secret}")
    private String secretKeyString;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity}") long accessValidTime,
            @Value("${jwt.refresh-token-validity}") long refreshValidTime
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.secretKeyString = secret;
        this.accessTokenValidTime = accessValidTime;
        this.refreshTokenValidTime = refreshValidTime;
    }

    // JWT 생성
    public JwtToken createJwtToken(String email) {
        long now = System.currentTimeMillis();

        // Access Token
        String accessToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessTokenValidTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token
        String refreshToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshTokenValidTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return new JwtToken(accessToken, refreshToken);
    }

    // 쿠키에서 Access Token 추출
    public Optional<String> extractAccessTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    log.info("Access Token이 쿠키에서 추출되었습니다: {}", cookie.getValue());
                    return Optional.of(cookie.getValue());
                }
            }
        }
        log.warn("Access Token이 쿠키에서 발견되지 않았습니다.");
        return Optional.empty();
    }


    // 헤더 값에서 "Bearer " 접두어를 필터링 + 제거하여 실제 토큰만 반환
    public Optional<String> extractBearerToken(String headerValue) {
        return Optional.ofNullable(headerValue)
                .filter(h -> h.startsWith(BEARER_PREFIX)) // "Bearer "로 시작하는지
                .map(h -> h.substring(BEARER_PREFIX.length())); // "Bearer " 제거
    }

    // 토큰에서 email 추출
    public String getEmailFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            log.error("[JwtService] 토큰 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            // parserBuilder + secretKey 로 검증
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.warn("[JwtService] 유효하지 않은 토큰: {}", e.getMessage());
            return false;
        }
    }

}
