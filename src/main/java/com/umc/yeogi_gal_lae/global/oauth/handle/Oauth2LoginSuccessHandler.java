package com.umc.yeogi_gal_lae.global.oauth.handle;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.global.jwt.JwtToken;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.CustomOauth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class Oauth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // CustomOauth2User에서 User 객체 가져오기
        CustomOauth2User customOauth2User = (CustomOauth2User) authentication.getPrincipal();
        User user = customOauth2User.getUser();

        // JWT 토큰 생성
        JwtToken jwtToken = jwtService.createJwtToken(user.getEmail());

        // Access Token을 HTTP 헤더에 설정
        Cookie accessCookie = new Cookie("accessToken", jwtToken.getAccessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false); // HTTPS 환경에서만 전송
        accessCookie.setPath("/"); // 전체 경로에 대해 유효
        accessCookie.setMaxAge((int) (jwtService.getRefreshTokenValidTime() / 1000)); // 초 단위
        response.addCookie(accessCookie);
        response.setHeader("Authorization", "Bearer " + jwtToken.getAccessToken());

        // Refresh Token을 쿠키에 설정 (옵션)
        Cookie refreshCookie = new Cookie("refreshToken", jwtToken.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // HTTPS 환경에서만 전송
        refreshCookie.setPath("/"); // 전체 경로에 대해 유효
        refreshCookie.setMaxAge((int) (jwtService.getRefreshTokenValidTime() / 1000)); // 초 단위
        response.addCookie(refreshCookie);

        log.info("OAuth2 로그인 성공. Access Token 및 Refresh Token 발급 완료.");

        // Redirect 또는 응답 처리 (JSON 응답 등)
        response.sendRedirect("/"); // 예: 홈 페이지로 리다이렉트
    }
}
