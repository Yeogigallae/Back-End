package com.umc.yeogi_gal_lae.global.oauth.oauth2user;

import java.util.Map;
import java.util.Objects;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo{

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }
    @Override
    public String getEmail(){
        return (String) attributes.get("email");
    }

    @Override
    public String getUsername(){
        return (String) attributes.get("login");
    }

    @Override
    public String getProfileImage() {
        return (String) attributes.get("profileImage");
    }
}
