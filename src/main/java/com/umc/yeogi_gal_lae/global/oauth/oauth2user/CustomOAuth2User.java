package com.umc.yeogi_gal_lae.global.oauth.oauth2user;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * CustomOAuth2User는 Spring Security OAuth2User를 확장(또는 래핑)한 클래스입니다.
 * 카카오/구글 등 소셜 로그인에서 가져온 정보(email, nickname, profileImage, authorities 등)를 담을 수 있습니다.
 */
@Getter
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oAuth2User;                     // 내부적으로 Spring Security의 OAuth2User를 보관
    private final String email;                              // 소셜 로그인에서 가져온 이메일
    private final String nickname;                           // 소셜 로그인에서 가져온 닉네임 -> DB 'username' 필드에 매핑
    private final String profileImage;                       // 소셜 로그인에서 가져온 프로필 이미지 URL
    private final Collection<? extends GrantedAuthority> authorities; // 권한 목록

    /**
     * @Builder를 통해 여러 파라미터를 손쉽게 주입받을 수 있습니다.
     * 빌더를 사용하는 곳에서 .authorities(...) 메서드를 호출하려면,
     * 생성자의 파라미터에도 authorities가 있어야 합니다.
     */
    @Builder
    public CustomOAuth2User(OAuth2User oAuth2User,
                            String email,
                            String nickname,
                            String profileImage,
                            Collection<? extends GrantedAuthority> authorities) {
        this.oAuth2User = oAuth2User;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.authorities = authorities;
    }

    /**
     * username을 nickname으로 매핑 (DB상 'username' 컬럼에 대응)
     */
    public String getUsername() {
        return nickname;
    }

    @Override
    public Map<String, Object> getAttributes() {
        // 내부의 oAuth2User가 null이 아닐 경우, 그 attributes를 반환
        return (oAuth2User != null) ? oAuth2User.getAttributes() : null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 빌더로 주입받은 authorities를 반환 (oAuth2User 내부 권한과 달라질 수 있음)
        return authorities;
    }

    @Override
    public String getName() {
        // 소셜 Provider 별로 'id' 또는 다른 키가 될 수 있음
        // kakao: userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName() -> "id"
        return (oAuth2User != null) ? oAuth2User.getName() : null;
    }
}
