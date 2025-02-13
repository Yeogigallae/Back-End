package com.umc.yeogi_gal_lae.api.user.dto.request;

import lombok.Getter;
import lombok.Setter;

public class UserRequestDTO {
    @Getter
    @Setter
    public static class LoginRequestDTO {
        private String email;
        private String nickname;
        private String profileImage;
    }
}
