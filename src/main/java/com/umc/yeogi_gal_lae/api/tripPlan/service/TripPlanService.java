package com.umc.yeogi_gal_lae.api.tripPlan.service;

import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMember;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.converter.TripPlanConverter;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanRequest;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanResponse;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.types.Status;
import com.umc.yeogi_gal_lae.api.tripPlan.types.TripPlanType;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteRoom;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRoomRepository;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.umc.yeogi_gal_lae.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class TripPlanService {
    private final TripPlanRepository tripPlanRepository;
    private final VoteRoomRepository voteRoomRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public TripPlanResponse createTripPlan(TripPlanRequest request, String userEmail, Long roomId,
        TripPlanType tripPlanType) {

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BusinessException(ROOM_NOT_FOUND));

        // 여행 계획 관련 검증 로직 호출
        validateTripPlanDays(request.getMinDays(), request.getMaxDays());

        TripPlan tripPlan = TripPlanConverter.toEntity(request, user, room, room.getName(),
            tripPlanType);
        tripPlanRepository.save(tripPlan);

        // 방 멤버들에게 투표 연결
        linkTripPlanToRoomMembers(tripPlan, room);

        createVoteRoomForTrip(tripPlan);

        return TripPlanConverter.toResponse(tripPlan);
    }

    private void validateTripPlanDays(Integer minDays, Integer maxDays) {
        if (minDays < 1 || maxDays < minDays) {
            throw new BusinessException(DATE_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public TripPlanResponse getTripPlanDetails(Long tripPlanId) {
        TripPlan tripPlan = tripPlanRepository.findById(tripPlanId)
                .orElseThrow(() -> new BusinessException.TripNotFoundException("여행 계획을 찾을 수 없습니다."));

        return TripPlanConverter.toResponse(tripPlan);
    }

    private void linkTripPlanToRoomMembers(TripPlan tripPlan, Room room) {
        List<RoomMember> members = room.getRoomMembers();
        if (members == null || members.isEmpty()) {
            throw new BusinessException(ROOM_MEMBER_NOT_EXIST);
        }
    }

    /**
     * 🚀 여행 계획이 생성되면 자동으로 투표방을 생성하는 메서드
     */
    private void createVoteRoomForTrip(TripPlan tripPlan) {
        // 기존에 존재하는 투표방이 있는지 확인 (중복 생성 방지)
        if (voteRoomRepository.findByTripPlanId(tripPlan.getId()).isPresent()) {
            throw new BusinessException(ErrorCode.VOTE_ROOM_ALREADY_EXISTS);
        }

        // 새로운 투표방 생성
        VoteRoom voteRoom = VoteRoom.builder()
            .tripPlan(tripPlan)
            .build();

        // 여행 계획과 투표방을 서로 연결
        voteRoom.setTripPlan(tripPlan);
        tripPlan.setVoteRoom(voteRoom);
        tripPlan.setStatus(Status.ONGOING); // 여행 계획 상태를 '진행 중'으로 변경

        // 저장
        voteRoomRepository.save(voteRoom);
        tripPlanRepository.save(tripPlan);
    }
}

