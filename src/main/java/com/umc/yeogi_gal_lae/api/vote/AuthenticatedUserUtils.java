package com.umc.yeogi_gal_lae.api.vote;

import com.umc.yeogi_gal_lae.global.oauth.oauth2user.CustomOauth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserUtils {

    private AuthenticatedUserUtils() {}

    public static String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomOauth2User) {
            //  OAuth2 로그인 사용자인 경우
            return ((CustomOauth2User) principal).getUser().getEmail();
        } else if (principal instanceof com.umc.yeogi_gal_lae.api.user.domain.User) {
            //  커스텀 User 객체
            return ((com.umc.yeogi_gal_lae.api.user.domain.User) principal).getEmail();
        } else if (principal instanceof UserDetails) {
            // UserDetails 인터페이스
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            //  Principal이 String 형태인 경우
            return (String) principal;
        } else {
            throw new IllegalArgumentException("알 수 없는 principal 타입: " + principal.getClass().getName());
        }
    }
}
