package com.umc.yeogi_gal_lae.api.room.service;

import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMember;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMemberId;
import com.umc.yeogi_gal_lae.api.room.dto.request.AddRoomMemberRequest;
import com.umc.yeogi_gal_lae.api.room.repository.RoomMemberRepository;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    /**
     * 방 멤버 추가
     *

     * @return 생성된 RoomMember
     */
    @Transactional
    public void addMembers(AddRoomMemberRequest request) {
        // 방 확인
        Optional<Room> roomOptional = roomRepository.findById(request.getRoomId());
        if (roomOptional.isEmpty()) {
            throw new IllegalArgumentException("Room not found with ID: " + request.getRoomId());
        }
        Room room = roomOptional.get();

        // 사용자 확인 및 멤버 추가
        List<Long> userIds = request.getUserIds();
        for (Long userId : userIds) {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                throw new IllegalArgumentException("User not found with ID: " + userId);
            }
            User user = userOptional.get();

            // RoomMember 생성
            RoomMember roomMember = RoomMember.builder()
                    .room(room)
                    .user(user)
                    .build();
            roomMemberRepository.save(roomMember);
        }
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