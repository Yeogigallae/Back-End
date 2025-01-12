package com.umc.yeogi_gal_lae.api.room.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomDto {
    private Long id; // 방 ID
    private String name; // 방 이름
}