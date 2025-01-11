package com.umc.yeogi_gal_lae.global.oauth.oauth2user;

import java.util.Map;

public abstract class Oauth2UserInfo {

    protected Map<String, Object> attributes;

    public Oauth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    public abstract String getEmail();
    public abstract String getName();
    public abstract String getImageUrl();
}
