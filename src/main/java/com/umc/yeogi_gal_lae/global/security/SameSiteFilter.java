package com.umc.yeogi_gal_lae.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SameSiteFilter extends OncePerRequestFilter {

    @Value("${jwt.secure}")
    private boolean secure;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName()) || "refreshToken".equals(cookie.getName())) {
                    StringBuilder cookieBuilder = new StringBuilder();
                    cookieBuilder.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
                    cookieBuilder.append("HttpOnly;");
                    cookieBuilder.append("Path=/;");
                    cookieBuilder.append("SameSite=Lax;"); // 필요에 따라 Strict 또는 None으로 변경
                    if (secure) {
                        cookieBuilder.append("Secure;");
                    }
                    response.addHeader("Set-Cookie", cookieBuilder.toString());
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
