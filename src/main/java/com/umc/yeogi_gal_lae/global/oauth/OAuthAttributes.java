package com.umc.yeogi_gal_lae.global.oauth;

import com.umc.yeogi_gal_lae.global.oauth.oauth2user.KakaoOAuth2UserInfo;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.OAuth2UserInfo;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.OAuth2UserInfoUtil;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * OAuthAttributes 클래스는 OAuth2User로부터 받은 사용자 정보를 기반으로 사용자 객체를 생성하는 역할을 합니다.
 * 현재는 카카오 소셜 로그인만 지원하도록 구현되었습니다.
 */
@Getter
@Slf4j
public class OAuthAttributes {

    private final String nameAttributeKey; // OAuth2 로그인 시 키(PK) 필드 값
    private final OAuth2UserInfo oAuth2UserInfo; // 카카오 로그인 유저 정보(유저네임, 이메일, 프로필이미지)

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    /**
     * OAuth2User에서 반환하는 사용자 정보를 기반으로 OAuthAttributes 객체를 생성합니다.
     *
     * @param registrationId OAuth2 클라이언트 등록 ID (여기서는 "kakao"만 지원)
     * @param userNameAttributeName 사용자 정보에서 키로 사용할 필드 이름
     * @param attributes OAuth2 제공자로부터 받은 사용자 정보 Map
     * @return OAuthAttributes 객체
     */
    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if ("kakao".equalsIgnoreCase(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        throw new IllegalArgumentException("지원되지 않는 registrationId: " + registrationId);
    }

    /**
     * 카카오 OAuth2UserInfo를 기반으로 OAuthAttributes 객체를 생성합니다.
     *
     * @param userNameAttributeName 사용자 정보에서 키로 사용할 필드 이름
     * @param attributes OAuth2 제공자로부터 받은 사용자 정보 Map
     * @return OAuthAttributes 객체
     */
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        String email = (String) OAuth2UserInfoUtil.getNestedAttribute(attributes, "kakao_account", "email")
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .orElseThrow(() -> new IllegalArgumentException("카카오 제공자로부터 이메일 정보를 받을 수 없습니다."));

        String username = (String) OAuth2UserInfoUtil.getNestedAttribute(attributes, "properties", "nickname")
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .orElse((String) OAuth2UserInfoUtil.getNestedAttribute(attributes, "kakao_account", "profile", "nickname")
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .orElse("Unknown"));

        String profileImage = (String) OAuth2UserInfoUtil.getNestedAttribute(attributes, "kakao_account", "profile", "profile_image_url")
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .orElse((String) OAuth2UserInfoUtil.getNestedAttribute(attributes, "properties", "profile_image")
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .orElse("default_profile_image_url"));

        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new KakaoOAuth2UserInfo(email, username, profileImage))
                .build();
    }

    /**
     * OAuth2UserInfo 객체를 통해 사용자 이메일을 추출합니다.
     *
     * @return 사용자 이메일
     */
    public String getEmail() {
        return oAuth2UserInfo.getEmail();
    }

    /**
     * OAuth2UserInfo 객체를 통해 사용자 이름을 추출합니다.
     *
     * @return 사용자 이름
     */
    public String getName() {
        return oAuth2UserInfo.getName();
    }

    /**
     * OAuth2UserInfo 객체를 통해 사용자 프로필 이미지를 추출합니다.
     *
     * @return 사용자 프로필 이미지 URL
     */
    public String getProfileImage() {
        return oAuth2UserInfo.getProfileImage();
    }
}
