package com.umc.yeogi_gal_lae.global.oauth.oauth2user;

import java.util.Map;

public class KakaoOauth2UserInfo extends Oauth2UserInfo {

    public KakaoOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getEmail() {
        // 카카오는 JSON 구조가 복잡할 수 있으므로 구조를 잘 확인해야 한다.
        // Ex) attributes.get("kakao_account") -> Map -> "email"
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) return null;
        return (String) kakaoAccount.get("email");
    }

    @Override
    public String getName() {
        // nickname
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) return null;

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null) return null;

        return (String) profile.get("nickname");
    }

    @Override
    public String getImageUrl() {
        // profile_image_url
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) return null;

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null) return null;

        return (String) profile.get("profile_image_url");
    }
}