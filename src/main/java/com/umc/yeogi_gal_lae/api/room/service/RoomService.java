package com.umc.yeogi_gal_lae.api.room.service;

import com.umc.yeogi_gal_lae.api.room.domain.RoomMember;
import com.umc.yeogi_gal_lae.api.room.dto.request.CreateRoomRequest;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public void createRoom(CreateRoomRequest request) {

        Room room = new Room(request.getName(),request.getMasterId());

        Room savedRoom = roomRepository.save(room);

    }

    //시안엔 없는데 방 삭제 기능 필요할 것 같음
//    @Transactional
//    public void deleteRoom(){
//
//    }
}