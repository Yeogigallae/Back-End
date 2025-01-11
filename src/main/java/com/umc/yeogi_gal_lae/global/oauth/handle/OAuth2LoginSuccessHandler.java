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
                                        Authentication authentication) {
        log.info("OAuth2 Login 성공!");

        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getEmail();
            String nickname = oAuth2User.getUsername(); // CustomOAuth2User에서 nickname 리턴
            String profileImage = oAuth2User.getProfileImage();

            // JWT 생성
            String accessToken = jwtService.createAccessToken(email);
            String refreshToken = jwtService.createRefreshToken();

            // DB에 user 저장 또는 업데이트
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        // 새로운 유저 생성
                        User newUser = User.builder()
                                .email(email)
                                .username(nickname)
                                .profileImage(profileImage)
                                .refreshToken(refreshToken)
                                .build();
                        return userRepository.save(newUser);
                    });

            // 이미 존재하는 유저라면 refreshToken 업데이트
            if (!refreshToken.equals(user.getRefreshToken())) {
                user.updateRefreshToken(refreshToken);
                userRepository.saveAndFlush(user);
            }

            // 쿠키에 accessToken, refreshToken 저장
            addTokenCookies(response, accessToken, refreshToken);

            // 로그인 성공 후 리다이렉트
            String redirectUrl = "http://localhost:5173"; // 프론트엔드 주소
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            log.error("OAuth2 Login 실패", e);
            throw new RuntimeException(e);
        }
    }

    private void addTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        log.info("Setting AccessToken and RefreshToken cookies");

        // Access Token 쿠키
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(secure);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 60); // 1시간
        // SameSite 속성은 아래 SameSiteFilter에서 처리하거나, 수동으로 붙일 수도 있음

        // Refresh Token 쿠키
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(secure);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 7); // 7일

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }
}
