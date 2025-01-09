package com.umc.yeogi_gal_lae.global.oauth.oauth2user;

import java.util.Collection;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final String email;
    private final String username;
    private final String profileImage;

    @Builder
    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes, String nameAttributeKey,
                            String email, String username, String profileImage) {
        super(authorities, attributes, nameAttributeKey);

        this.email = email;
        this.username = username;
        this.profileImage = profileImage;
    }
}
