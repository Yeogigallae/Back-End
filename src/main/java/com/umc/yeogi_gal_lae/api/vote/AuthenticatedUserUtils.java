package com.umc.yeogi_gal_lae.api.vote;

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

        if (principal instanceof com.umc.yeogi_gal_lae.api.user.domain.User) {
            // 커스텀 User 객체에서 이메일 추출
            return ((com.umc.yeogi_gal_lae.api.user.domain.User) principal).getEmail();
        } else if (principal instanceof UserDetails) {
            // UserDetails 에서 username 추출
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            // Principal 이 String 인 경우 처리
            return (String) principal;
        } else {
            throw new IllegalArgumentException("알 수 없는 principal 타입: " + principal.getClass().getName());
        }
    }
}
