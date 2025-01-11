package com.umc.yeogi_gal_lae.global.oauth;

import com.umc.yeogi_gal_lae.global.oauth.oauth2user.KakaoOAuth2UserInfo;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.OAuth2UserInfo;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.OAuth2UserInfoUtil;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * OAuthAttributes 클래스는 OAuth2User로부터 받은 사용자 정보를 기반으로
 * 사용자 객체를 생성/파싱하는 역할을 합니다.
 *
 * 현재는 카카오 소셜 로그인만 지원하도록 작성된 예시입니다.
 */
@Getter
@Slf4j
public class OAuthAttributes {

    private final String nameAttributeKey;  // OAuth2 로그인 시 PK로 쓸 필드 이름
    private final OAuth2UserInfo oauth2UserInfo; // 실제 소셜 로그인 유저 정보(이메일, 닉네임, 프로필이미지 등)

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    /**
     * OAuth2User에서 반환하는 사용자 정보를 기반으로 OAuthAttributes 객체 생성
     *
     * @param registrationId         OAuth2 클라이언트 등록 ID ("kakao" 등)
     * @param userNameAttributeName  사용자 정보에서 PK로 쓸 필드 이름
     * @param attributes             소셜로부터 받은 사용자 정보 (Map)
     * @return                       파싱된 OAuthAttributes 객체
     */
    public static OAuthAttributes of(String registrationId,
                                     String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if ("kakao".equalsIgnoreCase(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        throw new IllegalArgumentException("지원되지 않는 registrationId: " + registrationId);
    }

    /**
     * 카카오 사용자 정보를 파싱하여 OAuthAttributes 객체 생성
     *
     * (본 메서드를 private으로 유지하면, 외부에서는 of(...)만 호출 가능)
     */
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        // 1) 이메일
        String email = (String) OAuth2UserInfoUtil.getNestedAttribute(attributes, "kakao_account", "email")
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .orElseThrow(() -> new IllegalArgumentException("카카오에서 이메일 정보를 받을 수 없습니다."));

        // 2) 닉네임 (properties.nickname 우선, 없으면 kakao_account.profile.nickname)
        String name = (String) OAuth2UserInfoUtil.getNestedAttribute(attributes, "properties", "nickname")
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .orElseGet(() -> (String) OAuth2UserInfoUtil.getNestedAttribute(attributes, "kakao_account", "profile", "nickname")
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .orElse("Unknown")); // 닉네임 둘 다 없으면 "Unknown"

        // 3) 프로필 이미지 (kakao_account.profile.profile_image_url 우선, 없으면 properties.profile_image)
        String profileImage = (String) OAuth2UserInfoUtil.getNestedAttribute(attributes, "kakao_account", "profile", "profile_image_url")
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .orElseGet(() -> (String) OAuth2UserInfoUtil.getNestedAttribute(attributes, "properties", "profile_image")
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .orElse("default_profile_image_url")); // 둘 다 없으면 기본값

        // 4) KakaoOAuth2UserInfo 객체 생성
        KakaoOAuth2UserInfo kakaoUserInfo = new KakaoOAuth2UserInfo(email, name, profileImage);

        // 5) 빌더로 OAuthAttributes 생성
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)   // "id"
                .oauth2UserInfo(kakaoUserInfo)
                .build();
    }

    /**
     * 사용자 이메일 반환
     */
    public String getEmail() {
        return oauth2UserInfo.getEmail();
    }

    /**
     * 사용자 이름(닉네임) 반환
     */
    public String getName() {
        return oauth2UserInfo.getName();
    }

    /**
     * 사용자 프로필 이미지 URL 반환
     */
    public String getProfileImage() {
        return oauth2UserInfo.getProfileImage();
    }

}
