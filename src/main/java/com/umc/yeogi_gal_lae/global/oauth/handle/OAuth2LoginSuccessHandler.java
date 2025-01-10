package com.umc.yeogi_gal_lae.global.oauth.handle;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.CustomOAuth2User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Value("${jwt.secure}")
    private boolean secure;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("OAuth2 Login 성공!");

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
        String refreshToken = jwtService.createRefreshToken();

        // 이메일로 사용자가 이미 있는지 확인
        User user = userRepository.findByEmail(oAuth2User.getEmail())
                .orElseGet(() -> {
                    // 신규 사용자 생성
                    User newUser = User.builder()
                            .email(oAuth2User.getEmail())
                            .profileImage(oAuth2User.getProfileImage())
                            .refreshToken(refreshToken)
                            .build();
                    return userRepository.save(newUser);
                });

        // 기존 사용자의 Refresh Token 업데이트
        if (user.getRefreshToken() != null) {
            user.updateRefreshToken(refreshToken);
            userRepository.saveAndFlush(user);
        }

        // Access Token과 Refresh Token을 쿠키에 설정
        addTokenCookies(response, accessToken, refreshToken);

        // 로그인 성공 후 리다이렉트
        String redirectUrl = "http://localhost:5173"; // 프론트엔드 URL로 변경
        response.sendRedirect(redirectUrl);
    }

    /**
     * Access Token과 Refresh Token을 쿠키에 설정합니다.
     *
     * @param response     HTTP 응답 객체
     * @param accessToken  생성된 Access Token
     * @param refreshToken 생성된 Refresh Token
     */
    private void addTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        log.info("Setting AccessToken and RefreshToken cookies");

        // Access Token 쿠키 생성
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(secure); // 환경에 따라 설정
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60); // 1시간

        // Refresh Token 쿠키 생성
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(secure); // 환경에 따라 설정
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 7일

        // 응답에 쿠키 추가
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
}
