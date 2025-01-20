
package com.umc.yeogi_gal_lae.api.room.dto.request;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter

public class CreateRoomRequest {
    private String name; // 방 이름
    private List<Long> members;
    private User master;
}