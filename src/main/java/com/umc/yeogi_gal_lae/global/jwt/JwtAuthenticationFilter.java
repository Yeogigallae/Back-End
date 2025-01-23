package com.umc.yeogi_gal_lae.global.jwt;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // 1) 헤더 검사
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            // 2) 토큰 유효성 검사
            if (jwtService.validateToken(token)) {
                // 3) 토큰에서 email 추출
                String email = jwtService.getEmailFromToken(token);
                log.info("토큰에서 추출된 이메일: {}", email);

                // 4) DB에서 User 조회
                User user = userRepository.findByEmail(email).orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND)
                );

                // (선택) 권한 정보가 있다면, user.getAuthorities() 등을 가져올 수도 있음.
                // 여기서는 간단히 빈 Collections.emptyList() 사용.
                // 혹은 Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) 등 가능.
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                                user,                 // principal -> User 엔티티
                                null,                 // credentials
                                Collections.emptyList() // authorities
                        );

                // 5) SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("SecurityContext에 인증 정보를 설정했습니다. 사용자: {}", user.getId());
            } else {
                log.warn("유효하지 않은 JWT 토큰");
            }
        } catch (Exception e) {
            log.error("JWT 인증 중 오류 발생: {}", e.getMessage());
        }

        // 6) 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}
