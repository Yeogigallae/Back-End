package com.umc.yeogi_gal_lae.api.room.service;

import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMember;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMemberId;
import com.umc.yeogi_gal_lae.api.room.repository.RoomMemberRepository;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomMemberService {

    private final RoomMemberRepository roomMemberRepository;
    private final RoomRepository roomRepository;
//    private final UserRepository userRepository;

    /**
     * 방 멤버 추가
     *
     * @param roomId 방 ID
     * @param userId 사용자 ID
     * @return 생성된 RoomMember
     */
    @Transactional
    public RoomMember addMember(Long roomId, Long userId) {
        // 방 확인
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isEmpty()) {
            throw new IllegalArgumentException("Room not found with ID: " + roomId);
        }

//        // 사용자 확인
//        Optional<User> userOptional = userRepository.findById(userId);
//        if (userOptional.isEmpty()) {
//            throw new IllegalArgumentException("User not found with ID: " + userId);
//        }

        Room room = roomOptional.get();
//        User user = userOptional.get();

        // RoomMember 생성
        RoomMemberId roomMemberId = new RoomMemberId(roomId, userId);
        RoomMember roomMember = new RoomMember();
        roomMember.setId(roomMemberId);
        roomMember.setRoom(room);
//        roomMember.setUser(user);

        return roomMemberRepository.save(roomMember);
    }

    /**
     * 방에 있는 멤버 조회
     *
     * @param roomId 방 ID
     * @return 방에 있는 RoomMember 목록
     */
    public List<RoomMember> getMembersByRoomId(Long roomId) {
        return roomMemberRepository.findAllByIdRoomId(roomId);
    }

    /**
     * 특정 사용자가 속한 방 조회
     *
     * @param userId 사용자 ID
     * @return 사용자가 속한 RoomMember 목록
     */
    public List<RoomMember> getRoomsByUserId(Long userId) {
        return roomMemberRepository.findAllByIdUserId(userId);
    }
}