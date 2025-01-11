package com.umc.yeogi_gal_lae.domain.room.service;

import com.umc.yeogi_gal_lae.domain.room.domain.RoomMember;
import com.umc.yeogi_gal_lae.domain.room.dto.CreateRoomRequest;
import com.umc.yeogi_gal_lae.domain.room.dto.RoomDto;
import com.umc.yeogi_gal_lae.domain.room.domain.Room;
import com.umc.yeogi_gal_lae.domain.room.repository.RoomRepository;
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


        Room room = new Room();
        room.setName(request.getName());
        //room.setMasterId();

        Room savedRoom = roomRepository.save(room);

        List<Long> members = request.getMembers();
        for(Long memberId : members) {
            RoomMember roomMember = new RoomMember();
            roomMember.setRoom(savedRoom);

//            roomMember.setUserId(memberId);
        }
    }
}