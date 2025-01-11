package com.umc.yeogi_gal_lae.global.jwt;

import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.exception.BusinessException;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;
import com.umc.yeogi_gal_lae.global.response.Code;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final List<String> excludeUrlPatterns;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // Exclude the URLs from filter
        for (String pattern : excludeUrlPatterns) {
            if (pathMatcher.match(pattern, requestURI)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        try {
            // Authorization 헤더에서 토큰 추출
            String header = request.getHeader("Authorization");

            if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
                log.warn("JWT 토큰이 존재하지 않거나 Bearer로 시작하지 않습니다.");
                filterChain.doFilter(request, response);
                return;
            }

            // Bearer 제거
            String token = header.substring(7);
            log.debug("Extracted JWT Token: {}", token);

            // 토큰 유효성 검증
            if (jwtService.validateToken(token)) {
                String email = jwtService.getEmailFromToken(token);
                log.debug("Extracted Email from Token: {}", email);

                if (email != null) {
                    // DB에서 유저 조회
                    var userOptional = userRepository.findByEmail(email);
                    if (userOptional.isPresent()) {
                        var user = userOptional.get();

                        // 인증 토큰 생성
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        user, null, List.of() // 권한 리스트가 필요하다면 추가
                                );
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // SecurityContext에 인증 정보 설정
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        log.info("SecurityContext에 인증 정보를 설정했습니다. 사용자: {}", email);
                    } else {
                        log.warn("이메일 {}를 가진 사용자를 DB에서 찾을 수 없습니다.", email);
                        throw new BusinessException(Code.USER_NOT_FOUND);
                    }
                }
            } else {
                log.warn("유효하지 않은 JWT 토큰입니다.");
            }
        } catch (BusinessException ex) {
            log.error("BusinessException 발생: {}", ex.getMessage());
            // 필요 시, 에러 응답을 설정할 수 있습니다.
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            return; // 필터 체인 진행 중지
        } catch (Exception ex) {
            log.error("JWT 인증 필터 중 오류 발생: {}", ex.getMessage());
            // 필요 시, 에러 응답을 설정할 수 있습니다.
        }

        filterChain.doFilter(request, response);
    }
}
