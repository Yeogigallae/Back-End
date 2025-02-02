package com.umc.yeogi_gal_lae.api.user.controller;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.global.jwt.JwtToken;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;
import com.umc.yeogi_gal_lae.global.oauth.handle.Oauth2LoginSuccessHandler;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.CustomOauth2User;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final Oauth2LoginSuccessHandler oauth2LoginSuccessHandler;

    @PostMapping("/login")
    public Response<Object> login(HttpServletResponse response) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Response.of(ErrorCode.UNAUTHORIZED);
            }

            User user;
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomOauth2User) {
                user = ((CustomOauth2User) principal).getUser();
            } else if (principal instanceof User) {
                user = (User) principal;
            } else {
                return Response.of(ErrorCode.UNAUTHORIZED);
            }

            JwtToken jwtToken = jwtService.createJwtToken(user.getEmail());

            Cookie accessTokenCookie = oauth2LoginSuccessHandler.createAccessTokenCookie(jwtToken.getAccessToken(), 60 * 60);
            response.addCookie(accessTokenCookie);

            // Refresh Token 프론트 반환하지 않음
            user.setRefreshToken(jwtToken.getRefreshToken());
            userRepository.save(user);

            return Response.of(SuccessCode.USER_LOGIN_OK);
        } catch (Exception e) {
            return Response.of(ErrorCode.UNAUTHORIZED);
        }
    }

    @GetMapping("/check-cookie")
    public ResponseEntity<String> checkCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies(); // 요청에 포함된 쿠키 가져오기

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return ResponseEntity.ok("Access Token Found: " + cookie.getValue());
                }
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Access Token Cookie Not Found");
    }

}
