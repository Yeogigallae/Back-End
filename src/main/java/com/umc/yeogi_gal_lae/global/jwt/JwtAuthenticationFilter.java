package com.umc.yeogi_gal_lae.global.jwt;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.mapping.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        log.info("checkAccessTokenAndAuthentication() 호출");

        // 1) 쿠키나 헤더에서 Access Token 추출
        Optional<String> accessTokenOpt = jwtService.extractAccessTokenFromCookie(request);
        if (accessTokenOpt.isPresent()) {
            String accessToken = accessTokenOpt.get();
            log.info("Access Token이 쿠키에서 추출되었습니다: {}", accessToken);

            // 2) 토큰 검증
            if (jwtService.isTokenValid(accessToken)) {
                log.info("유효한 Access Token이 발견되었습니다: {}", accessToken);

                // 3) 이메일 추출
                String email = jwtService.extractEmail(accessToken);
                log.info("이메일이 추출되었습니다: {}", email);

                // 4) DB에서 email로 사용자 조회
                userRepository.findByEmail(email).ifPresentOrElse(user -> {
                    log.info("DB에서 사용자 발견: userId={}, email={}", user.getId(), user.getEmail());
                    saveAuthentication(user);
                }, () -> {
                    // 여기서 예외를 던지는 대신, 그냥 인증 없이 패스
                    log.info("DB에서 email={} 사용자 없음 → 아직 미생성 사용자로 간주, 인증 미적용", email);
                });
            } else {
                log.warn("Access Token이 유효하지 않습니다: {}", accessToken);
            }
        } else {
            log.warn("쿠키에서 Access Token을 찾을 수 없습니다.");
        }

        // 필터 체인 계속
        filterChain.doFilter(request, response);
    }

    /**
     * DB에서 찾은 사용자로 Spring Security Context에 인증 정보 저장
     */
    private void saveAuthentication(User user) {
        // principal = user.getEmail()
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password("N/A")
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("사용자 인증 정보가 저장되었습니다: userId={}, email={}", user.getId(), user.getEmail());
    }
}
