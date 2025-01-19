package com.umc.yeogi_gal_lae.api.room.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class AddRoomMemberRequest {
    private Long roomId;
    private List<Long> userIds;
}