package com.umc.yeogi_gal_lae.api.tripPlan.service;

import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.repository.RoomMemberRepository;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.converter.TripPlanConverter;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanRequest;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanResponse;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TripPlanService {
    private final TripPlanRepository tripPlanRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;

    @Transactional
    public TripPlanResponse createTripPlan(TripPlanRequest request, Long userId) {

        // User 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        // TripPlanType에 따라 처리
        switch (request.getTripPlanType()) {
            case COURSE:
                return createCourseVote(request, user);
            case SCHEDULE:
                return createScheduleVote(request, user);
            case BUDGET:
                return createBudgetVote(request, user);
            default:
                throw new IllegalArgumentException("일치하는 여행 계획 유형이 없습니다.");
        }
    }

    private TripPlanResponse createCourseVote(TripPlanRequest request, User user) {
        TripPlan tripPlan = TripPlanConverter.toEntity(request, user);
        tripPlanRepository.save(tripPlan);
        return TripPlanConverter.toResponse(tripPlan);
    }

    private TripPlanResponse createScheduleVote(TripPlanRequest request, User user) {
        TripPlan tripPlan = TripPlanConverter.toEntity(request, user);
        tripPlanRepository.save(tripPlan);
        return TripPlanConverter.toResponse(tripPlan);
    }

    private TripPlanResponse createBudgetVote(TripPlanRequest request, User user) {
        TripPlan tripPlan = TripPlanConverter.toEntity(request, user);
        tripPlanRepository.save(tripPlan);
        return TripPlanConverter.toResponse(tripPlan);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTripPlanForRoom(Long tripPlanId, Long roomId) {
        // TripPlan 조회
        TripPlan tripPlan = tripPlanRepository.findById(tripPlanId)
                .orElseThrow(() -> new IllegalArgumentException("해당 TripPlan ID를 찾을 수 없습니다."));

        // Room 조회 (유효성 검증)
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Room ID를 찾을 수 없습니다."));

        // 필요한 TripPlan 데이터를 반환할 Map 구성
        Map<String, Object> tripPlanData = new HashMap<>();
        tripPlanData.put("tripPlanId", tripPlan.getId());
        tripPlanData.put("name", tripPlan.getName());
        tripPlanData.put("location", tripPlan.getLocation());
        tripPlanData.put("startDate", tripPlan.getStartDate());
        tripPlanData.put("endDate", tripPlan.getEndDate());
        tripPlanData.put("tripPlanType", tripPlan.getTripPlanType());
        tripPlanData.put("tripType", tripPlan.getTripType());
        tripPlanData.put("imageUrl", tripPlan.getImageUrl());
        tripPlanData.put("groupId", tripPlan.getGroupId());

        // 방 정보를 반환 데이터에 포함 (선택적)
        tripPlanData.put("roomId", room.getId());
        tripPlanData.put("roomName", room.getName());

        return tripPlanData;
    }

    @Transactional(readOnly = true)
    public List<String> getAvailableImages() {
        // 예제: 정적 이미지 URL 리스트
        List<String> imageUrls = List.of(
                "https://example.com/images/1.jpg",
                "https://example.com/images/2.jpg",
                "https://example.com/images/3.jpg",
                "https://example.com/images/4.jpg",
                "https://example.com/images/5.jpg"
        );

        return imageUrls;
    }

//    // 진행 중인 투표 목록 조회
//    @Transactional(readOnly = true)
//    public List<TripPlanResponse> getOngoingVotes(Long userId) {
//        List<TripPlan> ongoingTrips = tripPlanRepository.findByUserIdAndStatus(userId, "ONGOING");
//        return ongoingTrips.stream()
//                .map(trip -> TripPlanConverter.toResponse(
//                        trip,
//                        calculateRemainingTime(trip)))
//                .collect(Collectors.toList());
//    }
//
//    // 예정된 투표 목록 조회
//    @Transactional(readOnly = true)
//    public List<TripPlanResponse> getPlannedTrips(Long userId) {
//        List<TripPlan> plannedTrips = tripPlanRepository.findByUserIdAndStatus(userId, "PLANNED");
//        return plannedTrips.stream()
//                .map(TripPlanConverter::toResponse)
//                .collect(Collectors.toList());
//    }
//
//    // 완료된 여행 목록 조회
//    @Transactional(readOnly = true)
//    public Map<String, List<TripPlanResponse>> getCompletedTrips(Long userId) {
//        List<TripPlan> completedTrips = tripPlanRepository.findByUserIdAndStatus(userId, "COMPLETED");
//        return completedTrips.stream()
//                .collect(Collectors.groupingBy(
//                        trip -> trip.getTripType().toString(),
//                        Collectors.mapping(TripPlanConverter::toResponse, Collectors.toList())
//                ));
//    }
//
//    // 상태 업데이트 로직
//    @Transactional
//    public void updateTripStatus(TripPlan tripPlan) {
//        long elapsedTime = Duration.between(tripPlan.getCreatedAt(), LocalDateTime.now()).toSeconds();
//        if (elapsedTime >= tripPlan.getVoteLimitTime().getSeconds()) {
//            tripPlan.setStatus("PLANNED");
//        }
//
//        long totalVotes = roomMemberRepository.countByRoomId(tripPlan.getUser().getId());
//        long votedUsers = tripPlanRepository.countVotesForTripPlanByUser(tripPlan.getId(), tripPlan.getUser().getId());
//
//        if (totalVotes == votedUsers) {
//            tripPlan.setStatus("PLANNED");
//        }
//
//        tripPlanRepository.save(tripPlan);
//    }
//
//    // 남은 시간 계산
//    private int calculateRemainingTime(TripPlan tripPlan) {
//        int totalSeconds = tripPlan.getVoteLimitTime().getSeconds();
//        long elapsedTime = Duration.between(tripPlan.getCreatedAt(), LocalDateTime.now()).toSeconds();
//        return Math.max(totalSeconds - (int) elapsedTime, 0);
//    }
}
