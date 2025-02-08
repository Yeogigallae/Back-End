package com.umc.yeogi_gal_lae.global.oauth.handle;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class Oauth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // CustomOauth2User 에서 User 객체 가져오기
        CustomOauth2User customOauth2User = (CustomOauth2User) authentication.getPrincipal();
        User user = customOauth2User.getUser();


        // JWT 토큰 생성
        JwtToken jwtToken = jwtService.createJwtToken(user.getEmail());
        log.info("User Email 입니다!! :  {}", user.getEmail());


        // SecurityContextHolder 에 인증 정보 저장
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Access Token을 쿠키에 저장 & DB 저장
        Cookie accessTokenCookie = createAccessTokenCookie(jwtToken.getAccessToken(), 60 * 60);
        response.addCookie(accessTokenCookie);

        log.info("Cookie Name:  {}", accessTokenCookie.getName());
        log.info("Cookie Value:  {}", accessTokenCookie.getValue());

        // 토큰 DB 저장
        user.setAccessToken(jwtToken.getAccessToken());
        userRepository.save(user);
        user.setRefreshToken(jwtToken.getRefreshToken());
        userRepository.save(user);

        log.info("OAuth2 로그인 성공. Access Token 및 Refresh Token 발급 완료.");

        // Redirect 또는 응답 처리 (JSON 응답 등)
        response.sendRedirect("http://localhost:5173/login/kakao");     // 프론트로 리다이렉팅
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"OAuth2 로그인 성공\", \"status\": \"success\"}");
    }

    public Cookie createAccessTokenCookie(String accessToken, int maxAge) {
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false); // HTTPS에서만 사용
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(maxAge);

        accessTokenCookie.setAttribute("SameSite", "Lax");  // 또는 "None"으로 변경
        return accessTokenCookie;
    }

//    public Cookie createRefreshTokenCookie(String refreshToken, int maxAge) {
//        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setSecure(false); // HTTPS에서만 사용
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setMaxAge(maxAge);
//        return refreshTokenCookie;
//    }
}
