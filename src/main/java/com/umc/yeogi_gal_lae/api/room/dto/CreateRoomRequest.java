
package com.umc.yeogi_gal_lae.api.room.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateRoomRequest {
    private String name; // 방 이름
    private List<Long> members;
}