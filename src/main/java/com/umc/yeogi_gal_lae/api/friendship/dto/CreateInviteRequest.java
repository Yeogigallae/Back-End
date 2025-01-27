package com.umc.yeogi_gal_lae.api.friendship.dto;

import lombok.Data;

@Data
public class CreateInviteRequest {
    private Long inviterId;      // 초대한 사용자 ID
    private String inviteeEmail; // 초대받는 사용자의 이메일
}