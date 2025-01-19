package com.umc.yeogi_gal_lae.api.vote;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserUtils {

    private AuthenticatedUserUtils() {}

    public static String getAuthenticatedUserEmail(){

        // 현재 인증된 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal != null && principal.getClass().getName().equals("User")) {    // Principal 의 객체 타입 확인
            return ((UserDetails) principal).getUsername();
        }
        else if (principal != null) { return  principal.toString();}
        else { throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다."); }
    }
}
