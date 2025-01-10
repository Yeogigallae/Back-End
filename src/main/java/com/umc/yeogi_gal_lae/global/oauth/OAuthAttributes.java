package com.umc.yeogi_gal_lae.global.oauth;

import com.umc.yeogi_gal_lae.global.oauth.oauth2user.KakaoOAuth2UserInfo;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.OAuth2UserInfo;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {

    private final String nameAttributeKey; // OAuth2 로그인 시 키(PK) 필드 값
    private final OAuth2UserInfo oAuth2UserInfo; // 카카오 로그인 유저 정보(유저네임, 이메일, 프로필이미지)

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    /*
     * OAuth2User에서 반환하는 사용자 정보를 기반으로 OAuthAttributes 객체 생성*
     */
    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if ("kakao".equalsIgnoreCase(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

}
