package com.umc.yeogi_gal_lae.api.room.service;

import com.umc.yeogi_gal_lae.api.room.domain.RoomMember;
import com.umc.yeogi_gal_lae.api.room.dto.request.CreateRoomRequest;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {


    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    public void createRoom(CreateRoomRequest request, String token) {

        // 1. JWT 토큰에서 요청자의 이메일 추출
        String email = jwtService.getEmailFromToken(token);
        if (email == null) {
            throw new IllegalArgumentException("유효하지 않은 JWT 토큰입니다.");
        }

        // 2. 요청자의 User 객체 조회
        User master = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        // 3. Room 생성 및 저장
        Room room = new Room(request.getName(), master); // master를 User 객체로 설정
        roomRepository.save(room);
    }
    //시안엔 없는데 방 삭제 기능 필요할 것 같음
//    @Transactional
//    public void deleteRoom(){
//
//    }
}