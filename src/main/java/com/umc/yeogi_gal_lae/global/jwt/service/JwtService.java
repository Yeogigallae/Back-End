package com.umc.yeogi_gal_lae.global.jwt.service;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer";

    private final UserRepository userRepository;

    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    // accessToken 생성 메소드
    public String createAccessToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(ACCESS_TOKEN_SUBJECT)
                .setExpiration(new Date(now.getTime() + accessTokenExpirationPeriod))
                .claim(EMAIL_CLAIM, email)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(REFRESH_TOKEN_SUBJECT)
                .setExpiration(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String reIssueRefreshToken(User user) {
        String reIssuedRefreshToken = this.createRefreshToken();
        user.updateRefreshToken(reIssuedRefreshToken);
        userRepository.saveAndFlush(user);
        return reIssuedRefreshToken;
    }

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

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }

    public String extractEmail(String accessToken) {
        Claims claims = decodeAccessToken(accessToken);
        if (claims != null) {
            String email = claims.get("email", String.class);
            return email;
        }
        return null;
    }

    private Claims decodeAccessToken(String accessToken) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }


}
