package com.umc.yeogi_gal_lae.global.oauth.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        String referer = request.getHeader("Referer");  // 요청을 보낸 URL 확인
        String redirectUrl = (referer != null && referer.contains("localhost:5173"))
            ? "http://localhost:5173"
            : "https://yeogi.my";

        log.info("🔹 로그인 성공! 프론트엔드로 리디렉트: " + redirectUrl);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl + "/login/success");
    }
}
