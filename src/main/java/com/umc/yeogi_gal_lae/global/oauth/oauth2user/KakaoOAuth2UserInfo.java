package com.umc.yeogi_gal_lae.global.oauth.oauth2user;

import com.umc.yeogi_gal_lae.global.oauth.oauth2user.OAuth2UserInfo;

/**
 * KakaoOAuth2UserInfo 클래스는 카카오 OAuth2 제공자로부터 받은 사용자 정보를 추출하는 역할을 합니다.
 */
public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    private final String email;
    private final String name;
    private final String profileImage;

    public KakaoOAuth2UserInfo(String email, String name, String profileImage) {
        super(null);
        this.email = email;
        this.name = name;
        this.profileImage = profileImage;
    }

    @Override
    public String getEmail(){
        return email;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public String getProfileImage() {
        return profileImage;
    }
}
