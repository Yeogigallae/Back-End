package com.umc.yeogi_gal_lae.global.oauth;

import com.umc.yeogi_gal_lae.global.oauth.oauth2user.KakaoOauth2UserInfo;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.Oauth2UserInfo;
import java.util.Map;

public class OauthAttributes {

    public static Oauth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return new KakaoOauth2UserInfo(attributes);
        }
        throw new IllegalArgumentException("Unsupported provider: " + registrationId);
    }
}
