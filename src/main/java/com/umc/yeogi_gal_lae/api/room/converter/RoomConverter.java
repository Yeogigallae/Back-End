package com.umc.yeogi_gal_lae.api.room.converter;

import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.dto.CreateRoomRequest;
import com.umc.yeogi_gal_lae.api.room.dto.RoomResponse;

public class RoomConverter {

    public static Room toRoomEntity(CreateRoomRequest request, Long masterId) {
        return Room.builder()
                .name(request.getName())
                .masterId(masterId)
                .build();
    }

    public static RoomResponse toRoomResponse(Room room) {
        return RoomResponse.builder()
                .roomId(room.getId())
                .name(room.getName())
                .masterId(room.getMasterId())
                .build();
    }
}
