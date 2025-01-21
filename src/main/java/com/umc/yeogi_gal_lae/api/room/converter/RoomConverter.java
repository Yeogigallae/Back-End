package com.umc.yeogi_gal_lae.api.room.converter;

import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.dto.response.RoominfoResponse;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class RoomConverter {

    public static RoominfoResponse toResponse(Room room) {
        return RoominfoResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .master(room.getMaster())
                .build();
    }

    public static Room fromRequest(String name, User master) {
        return Room.builder()
                .name(name)
                .master(master)
                .build();
    }
}