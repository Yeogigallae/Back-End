package com.umc.yeogi_gal_lae.global.oauth.oauth2user;

import java.util.Map;
import java.util.Optional;

/**
 * KakaoOAuth2UserInfo 클래스는 카카오 OAuth2 제공자로부터 받은 사용자 정보를 추출하는 역할을 합니다.
 */
public class KakaoOAuth2UserInfo extends OAuth2UserInfo{

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    /**
     * 사용자 이메일을 반환합니다.
     *
     * @return 사용자 이메일
     */
    @Override
    public String getEmail(){
        return (String) OAuth2UserInfoUtil.getNestedAttribute(attributes, "kakao_account", "email")
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .orElse(null);
    }

    /**
     * 사용자 이름(닉네임)을 반환합니다.
     *
     * @return 사용자 닉네임
     */
    @Override
    public String getUsername(){
        // properties.nickname 또는 kakao_account.profile.nickname에서 닉네임 추출
        return (String) OAuth2UserInfoUtil.getNestedAttribute(attributes, "properties", "nickname")
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .orElse((String) OAuth2UserInfoUtil.getNestedAttribute(attributes, "kakao_account", "profile", "nickname")
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .orElse("Unknown"));
    }

    /**
     * 사용자 프로필 이미지 URL을 반환합니다.
     *
     * @return 사용자 프로필 이미지 URL
     */
    @Override
    public String getProfileImage() {
        // kakao_account.profile.profile_image_url 또는 properties.profile_image에서 이미지 URL 추출
        return (String) OAuth2UserInfoUtil.getNestedAttribute(attributes, "kakao_account", "profile", "profile_image_url")
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .orElse((String) OAuth2UserInfoUtil.getNestedAttribute(attributes, "properties", "profile_image")
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .orElse("default_profile_image_url"));
    }
}
