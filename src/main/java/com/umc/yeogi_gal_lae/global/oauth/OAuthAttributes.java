package com.umc.yeogi_gal_lae.global.oauth;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String profileImage;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String profileImage) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        // 다른 소셜 로그인 제공자에 대한 처리 추가 가능
        throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String profileImage = (String) profile.get("profile_image_url"); // Kakao API의 프로필 이미지 URL 키

        return OAuthAttributes.builder()
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .name((String) profile.get("nickname")) // Kakao API의 닉네임 키
                .email(email)
                .profileImage(profileImage)
                .build();
    }
}
