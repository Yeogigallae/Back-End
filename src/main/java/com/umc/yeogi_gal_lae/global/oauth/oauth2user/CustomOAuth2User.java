package com.umc.yeogi_gal_lae.global.oauth.oauth2user;

import java.util.Collection;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * CustomOAuth2User 클래스는 OAuth2User 인터페이스를 구현하여, 추가적인 사용자 정보를 포함합니다.
 */
@Getter
public class CustomOAuth2User implements OAuth2User {
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String profileImage;

    @Builder
    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes,
                            String nameAttributeKey, String name, String email, String profileImage) {
        this.authorities = authorities;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }

}
