package com.umc.yeogi_gal_lae.api.room.service;

import com.umc.yeogi_gal_lae.api.room.converter.RoomConverter;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMember;
import com.umc.yeogi_gal_lae.api.room.dto.CreateRoomRequest;
import com.umc.yeogi_gal_lae.api.room.dto.RoomResponse;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.CustomOauth2User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        // 1) 현재 로그인한 사용자 ID 가져오기 (예시)
        Long currentUserId = getCurrentUserId();

        // 2) Converter 사용해서 Room 엔티티 생성
        Room room = RoomConverter.toRoomEntity(request, currentUserId);

        // 3) DB 저장
        Room savedRoom = roomRepository.save(room);

        // 4) RoomMember 로직 예시
        List<Long> members = request.getMembers();
        if (members != null) {
            for (Long memberId : members) {
                RoomMember roomMember = new RoomMember();
                roomMember.setRoom(savedRoom);
                // roomMember.setUserId(memberId);
                // roomMemberRepository.save(roomMember);
            }
        }

        // 5) Converter 사용해서 응답 DTO 생성
        return RoomConverter.toRoomResponse(savedRoom);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            // 인증 정보가 없으면 BusinessException 던짐
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomOauth2User)) {
            // CustomOauth2User 아닌 경우 BusinessException 던짐
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        CustomOauth2User customOauth2User = (CustomOauth2User) principal;
        // DB 상 PK를 반환
        return customOauth2User.getUser().getId();
    }
}
