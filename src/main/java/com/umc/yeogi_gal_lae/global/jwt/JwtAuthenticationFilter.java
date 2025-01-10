package com.umc.yeogi_gal_lae.global.jwt;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    // 요청에서 Access Token을 추출하고, 토큰의 유효성을 검증한 후,
    // 유효한 토큰일 경우 해당 사용자의 인증 정보를 SecurityContext에 저장합니다.
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");

        // 요청의 쿠키에서 Access Token 추출
        jwtService.extractAccessTokenFromCookie(request)
                .filter(jwtService::isTokenValid) // 토큰의 유효성 검증
                .ifPresent(accessToken -> {
                    log.info("유효한 Access Token이 발견되었습니다: {}", accessToken);

                    // Access Token에서 이메일 추출
                    String email = jwtService.extractEmail(accessToken);
                    if (email != null) {
                        log.info("이메일이 추출되었습니다: {}", email);

                        // 이메일을 기반으로 사용자 정보 조회
                        userRepository.findByUsername(email)
                                .ifPresent(user -> {
                                    log.info("사용자 정보가 발견되었습니다: {}", user);
                                    saveAuthentication(user); // 인증 정보 저장
                                    log.info("사용자 인증 정보가 저장되었습니다: {}", user);
                                });
                    } else {
                        log.warn("이메일 추출 실패.");
                    }
                });

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * 사용자 정보를 기반으로 Authentication 객체를 생성하고, 이를 SecurityContext에 설정합니다.
     * @param user 인증할 사용자 객체
     */
    public void saveAuthentication(User user) {
        // Spring Security의 UserDetails 객체 생성
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password("") // 소셜 로그인은 비밀번호가 필요 없으므로 빈 문자열 설정
                .build();

        // Authentication 객체 생성
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        // SecurityContext에 Authentication 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
