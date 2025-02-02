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
        // CustomOauth2User 에서 User 객체 가져오기
        CustomOauth2User customOauth2User = (CustomOauth2User) authentication.getPrincipal();
        User user = customOauth2User.getUser();

        // JWT 토큰 생성
        JwtToken jwtToken = jwtService.createJwtToken(user.getEmail());

        // Access Token 을 HTTP 헤더에 설정
        Cookie accessTokenCookie = createAccessTokenCookie(jwtToken.getAccessToken(), (int) (jwtService.getAccessTokenValidTime() / 1000));
        response.addCookie(accessTokenCookie);

        // Refresh Token을 쿠키에 설정 (옵션)
        Cookie refreshTokenCookie = createRefreshTokenCookie(jwtToken.getRefreshToken(), (int) (jwtService.getRefreshTokenValidTime() / 1000));
        response.addCookie(refreshTokenCookie);

        log.info("OAuth2 로그인 성공. Access Token 및 Refresh Token 발급 완료.");

        // Redirect 또는 응답 처리 (JSON 응답 등)
        response.sendRedirect("/"); // 예: 홈 페이지로 리다이렉트
    }

    public Cookie createAccessTokenCookie(String accessToken, int maxAge) {
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // HTTPS에서만 사용
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(maxAge);
        return accessTokenCookie;
    }

    public Cookie createRefreshTokenCookie(String refreshToken, int maxAge) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS에서만 사용
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(maxAge);
        return refreshTokenCookie;
    }
}
