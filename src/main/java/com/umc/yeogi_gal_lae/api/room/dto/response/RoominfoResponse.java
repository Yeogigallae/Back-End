package com.umc.yeogi_gal_lae.api.room.dto.response;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RoominfoResponse {
    private Long id; // 방 ID
    private String name; // 방 이름
    private User master;
}