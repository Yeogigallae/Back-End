package com.umc.yeogi_gal_lae.api.room.dto.response;

import com.umc.yeogi_gal_lae.api.room.domain.RoomMemberId;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomMemberResponse {
    private RoomMemberId id;
    private Long roomId;
    private Long userId;
    private String profileImage;

}