package com.umc.yeogi_gal_lae.api.room.service;

import com.umc.yeogi_gal_lae.api.room.converter.RoomMemberConverter;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMember;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMemberId;
import com.umc.yeogi_gal_lae.api.room.dto.request.AddRoomMemberRequest;
import com.umc.yeogi_gal_lae.api.room.dto.response.RoomMemberResponse;
import com.umc.yeogi_gal_lae.api.room.repository.RoomMemberRepository;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomMemberService {

    private final RoomMemberRepository roomMemberRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;


    /**
     * 방 멤버 추가
     *

     * @return 생성된 RoomMember
     */
    @Transactional
    public void addMembers(AddRoomMemberRequest request, String token) {
        // 1. JWT에서 요청자의 email 추출
        String requesterEmail = jwtService.getEmailFromToken(token);
        if (requesterEmail == null) {
            throw new IllegalArgumentException("유효하지 않은 JWT 토큰입니다.");
        }

        // 2. 요청자 확인 및 권한 검증
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("요청자를 찾을 수 없습니다: " + requesterEmail));

        // 방 확인
        Optional<Room> roomOptional = roomRepository.findById(request.getRoomId());
        if (roomOptional.isEmpty()) {
            throw new IllegalArgumentException("해당하는 Room Id가 없습니다: " + request.getRoomId());
        }
        Room room = roomOptional.get();

        // 방에 대한 권한 확인 (예: 방 생성자인지 확인)
        if (!room.getMaster().equals(requester)) {
            throw new IllegalArgumentException("해당 방에 멤버를 추가할 권한이 없습니다.");
        }

        // 3. 사용자 확인 및 멤버 추가
        List<Long> userIds = request.getUserIds();
        for (Long userId : userIds) {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                throw new IllegalArgumentException("해당하는 User Id가 없습니다: " + userId);
            }
            User user = userOptional.get();

            RoomMember roomMember = RoomMemberConverter.fromRequest(room, user);
            roomMemberRepository.save(roomMember);
        }
    }

    /**
     * 방에 있는 멤버 조회
     */
    public List<RoomMemberResponse> getMembersByRoomId(Long roomId) {
        List<RoomMember> roomMembers = roomMemberRepository.findAllByIdRoomId(roomId);

        // RoomMember -> RoomMemberResponse 변환 (Converter 사용)
        return roomMembers.stream()
                .map(RoomMemberConverter::toResponse)
                .toList();
    }

    /**
     * 특정 사용자가 속한 방 조회
     */

    public List<RoomMemberResponse> getRoomsByUserId(Long userId) {
        List<RoomMember> roomMembers = roomMemberRepository.findAllByIdUserId(userId);

        // RoomMember -> RoomMemberResponse 변환 (Converter 사용)
        return roomMembers.stream()
                .map(RoomMemberConverter::toResponse)
                .toList();
    }
}