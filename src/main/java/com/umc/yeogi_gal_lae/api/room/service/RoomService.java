package com.umc.yeogi_gal_lae.api.room.service;

import com.umc.yeogi_gal_lae.api.room.converter.RoomMemberConverter;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMember;
import com.umc.yeogi_gal_lae.api.room.dto.request.CreateRoomRequest;
import com.umc.yeogi_gal_lae.api.room.dto.response.RoomMemberResponse;
import com.umc.yeogi_gal_lae.api.room.dto.response.RoominfoResponse;
import com.umc.yeogi_gal_lae.api.room.repository.RoomMemberRepository;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.error.BusinessException;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final UserRepository userRepository;

    /**
     * 방 생성
     *
     * @param request 방 생성 요청 정보
     */
    public void createRoom(CreateRoomRequest request, String userEmail) {
        // 유저 검증 (방장)
        User master = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException.UserNotFoundException("요청하신 이메일과 일치하는 유저가 존재하지 않습니다."));

        // 방 생성 및 저장: 한 명이 방을 여러개 만들어도 되니까 db 찾아보는 과정 필요 x
        Room room = Room.builder()
                .name(request.getRoomName())
                .master(master)
                .build();

        roomRepository.save(room);

        // 방 멤버 추가
        List<User> users = userRepository.findAllById(request.getUserIds());

        List<RoomMember> newRoomMembers = users.stream()
                .map(user -> RoomMemberConverter.fromRequest(room, user))
                .toList();


        roomMemberRepository.saveAll(newRoomMembers);

    }

    /**
     * 방 정보 조회
     *
     * @param roomId    방 ID
     * @return RoomResponse (방 정보 DTO)
     */
    public RoominfoResponse getRoomDetails(Long roomId) {
        // 방 존재 확인
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException.RoomNotFoundException("요청하신 방이 존재하지 않습니다."));

        // 방 정보 응답
        return RoominfoResponse.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .masterName(room.getMaster().getUsername())
                .build();
    }

    /**
     * 방에 속한 사용자 목록 조회
     *
     * @param roomId 방 ID
     * @return 사용자 이름 목록
     */
    public List<RoomMemberResponse> getRoomMembers(Long roomId) {
//        // 방 존재 확인
//        if (!roomRepository.existsById(roomId)) {
//            throw new EntityNotFoundException("방을 찾을 수 없습니다.");
//        }

        // 방에 속한 사용자 목록 조회
        List<RoomMember> roomMembers = roomMemberRepository.findAllByRoomId(roomId);

        // RoomMember 객체를 RoomMemberResponse로 변환
        return roomMembers.stream()
                .map(roomMember -> RoomMemberConverter.toResponse(roomMember)) // RoomMember의 User 이름
                .toList();
    }
}