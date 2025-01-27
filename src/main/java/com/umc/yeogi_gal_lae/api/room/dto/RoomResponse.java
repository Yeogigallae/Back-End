package com.umc.yeogi_gal_lae.api.room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class RoomResponse {
    private Long roomId; // 방 ID
    private String name; // 방 이름
    private Long masterId;
}