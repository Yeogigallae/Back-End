package com.umc.yeogi_gal_lae.api.room.converter;

import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMember;

import com.umc.yeogi_gal_lae.api.room.dto.response.RoomMemberResponse;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class RoomMemberConverter {

    public static RoomMemberResponse toResponse(RoomMember roomMember) {
        return RoomMemberResponse.builder()
                .id(roomMember.getId())
                .roomId(roomMember.getRoom().getId())
                .user(roomMember.getUser())
                .build();
    }

    public static RoomMember fromRequest(Room room, User user) {
        return RoomMember.builder()
                .room(room)
                .user(user)
                .build();
    }
}