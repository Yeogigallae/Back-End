package com.umc.yeogi_gal_lae.api.user.controller;

import com.umc.yeogi_gal_lae.api.vote.AuthenticatedUserUtils;
import com.umc.yeogi_gal_lae.global.oauth.handle.Oauth2LoginSuccessHandler;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.CustomOauth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final Oauth2LoginSuccessHandler oauth2LoginSuccessHandler;

    @GetMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 현재 인증된 사용자 이메일 가져오기
            String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();
            log.info("현재 인증된 사용자 이메일: {}", userEmail);

            // 현재 인증 객체 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("인증되지 않은 사용자 요청");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "사용자가 인증되지 않았습니다."));
            }

            log.info("인증 정보: {}", authentication);

            // 기존 Oauth2LoginSuccessHandler 로직 실행 (쿠키 설정 포함)
            oauth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

            // 성공 메시지 반환
            log.info("OAuth2 로그인 성공 - 쿠키 반환 완료");
            return ResponseEntity.ok().body(Map.of(
                    "message", "OAuth2 로그인 성공",
                    "status", "success",
                    "email", userEmail
            ));

        } catch (IllegalArgumentException e) {
            log.error("사용자 인증 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "사용자가 인증되지 않았습니다.", "error", e.getMessage()));
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "로그인 처리 중 오류 발생", "error", e.getMessage()));
        }
    }
}
