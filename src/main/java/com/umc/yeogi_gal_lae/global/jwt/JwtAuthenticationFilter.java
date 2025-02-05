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
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            if (jwtService.validateToken(token)) {
                String email = jwtService.getEmailFromToken(token);
                log.info("토큰에서 추출된 이메일: {}", email);

                // 1) DB에서 해당 email을 가진 User 찾기
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new BusinessException(
                                ErrorCode.USER_NOT_FOUND
                        ));

                // 2) principal을 User로 설정
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                                user, // principal → User 엔티티
                                null,
                                Collections.emptyList() // 권한 정보 (필요 시 추가)
                        );

                // 3) SecurityContext에 인증 정보 등록
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("SecurityContext에 인증 정보를 설정했습니다. 사용자: {}", user.getId());
            } else {
                log.warn("유효하지 않은 JWT 토큰");
            }
        } catch (Exception e) {
            log.error("JWT 인증 중 오류 발생: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
