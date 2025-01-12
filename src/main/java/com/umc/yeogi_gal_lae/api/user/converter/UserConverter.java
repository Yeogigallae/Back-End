package com.umc.yeogi_gal_lae.api.user.converter;

import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.dto.response.UserInfoResponse;

public class UserConverter {
    public static UserInfoResponse ofUserInfoResponse(User user) {
        return UserInfoResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .build();
    }
}

