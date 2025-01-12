package com.umc.yeogi_gal_lae.global.oauth.handle;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class Oauth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception)
            throws IOException, ServletException {

        // 실패 시 처리 로직 (로그 기록, 에러메시지 세팅, 리다이렉트 등)
        response.sendRedirect("/login?error=" + exception.getMessage());
    }
}
