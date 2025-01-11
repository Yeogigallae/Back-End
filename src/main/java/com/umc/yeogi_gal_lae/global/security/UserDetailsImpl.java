package com.umc.yeogi_gal_lae.global.security;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final String email;
    private final String profileImage;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String email, String profileImage, Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.profileImage = profileImage;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        // 권한 설정 필요 시 추가
        return new UserDetailsImpl(
                user.getEmail(),
                user.getProfileImage(),
                Collections.emptyList() // 권한이 없는 경우
        );
    }

    @Override
    public String getPassword() {
        return null; // OAuth2 로그인이므로 비밀번호는 필요 없음
    }

    @Override
    public String getUsername() {
        return email;
    }

    // 나머지 메서드 기본값 반환
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
