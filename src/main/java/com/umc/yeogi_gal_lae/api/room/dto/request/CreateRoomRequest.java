
package com.umc.yeogi_gal_lae.api.room.dto.request;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter

public class CreateRoomRequest {
    private String name; // 방 이름
    private List<Long> members;
    private Long masterId;
}