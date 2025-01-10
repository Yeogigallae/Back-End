package com.umc.yeogi_gal_lae.global.oauth.handle;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.CustomOAuth2User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        log.info("OAuth2 Login 성공!");

        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getEmail();

            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("이메일 정보가 누락되었습니다.");
            }

            // JWT 토큰 생성
            String accessToken = jwtService.createAccessToken(email);
            String refreshToken = jwtService.createRefreshToken();

            // 사용자 조회 및 Refresh Token 업데이트
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

            user.updateRefreshToken(refreshToken);
            userRepository.saveAndFlush(user);

            // 쿠키에 Access Token과 Refresh Token 저장
            addTokenCookies(response, accessToken, refreshToken);

            // 로그인 성공 후 리디렉션
            String redirectUrl = "http://localhost:5173"; // 클라이언트 애플리케이션 URL
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생: {}", e.getMessage());
            // 인증 실패 핸들러로 예외 전달 (선택 사항)
            // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * Access Token과 Refresh Token을 쿠키에 설정합니다.
     *
     * @param response     HTTP 응답 객체
     * @param accessToken  생성된 Access Token
     * @param refreshToken 생성된 Refresh Token
     */
    private void addTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        // Access Token 쿠키 생성
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // 프로덕션 환경에서는 반드시 true로 설정
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60); // 1시간

        // Refresh Token 쿠키 생성
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // 프로덕션 환경에서는 반드시 true로 설정
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 7일

        // 응답에 쿠키 추가
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
}
