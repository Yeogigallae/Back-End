package com.umc.yeogi_gal_lae.global.oauth.oauth2user;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@ToString
public class CustomOauth2User extends DefaultOAuth2User {

    private final User user;

    /**
     * 생성자
     *
     * @param authorities      사용자의 권한 컬렉션
     * @param attributes       OAuth2 제공자로부터 받은 사용자 속성
     * @param nameAttributeKey 사용자 이름 속성 키
     * @param user             애플리케이션의 User 엔티티
     */
    public CustomOauth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            User user) {
        super(authorities, attributes, nameAttributeKey);
        this.user = user;
    }

    /**
     * User 엔티티를 반환하는 메서드
     *
     * @return User 엔티티
     */
    public User getUser() {
        return this.user;
    }
}
