
package com.umc.yeogi_gal_lae.api.room.dto.request;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CreateRoomRequest {
    private String roomName; // 방 이름
    private List<Long> members;
}