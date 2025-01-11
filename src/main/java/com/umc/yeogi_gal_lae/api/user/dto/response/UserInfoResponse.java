package com.umc.yeogi_gal_lae.api.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInfoResponse {
    private Long userId;
    private String username;
    private String email;
    private String profileImage;
}
