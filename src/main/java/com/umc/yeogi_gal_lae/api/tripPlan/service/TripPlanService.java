package com.umc.yeogi_gal_lae.api.tripPlan.service;

import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMember;
import com.umc.yeogi_gal_lae.api.room.repository.RoomMemberRepository;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.converter.TripPlanConverter;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanRequest;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanResponse;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.types.TripPlanType;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.api.userImage.domain.UserImage;
import com.umc.yeogi_gal_lae.api.userImage.repository.UserImageRepository;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.umc.yeogi_gal_lae.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class TripPlanService {
    private final TripPlanRepository tripPlanRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final UserImageRepository userImageRepository;

    @Transactional
    public TripPlanResponse createTripPlan(TripPlanRequest request, Long userId, Long roomId, TripPlanType tripPlanType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ROOM_NOT_FOUND));

        // 여행 계획 관련 검증 로직 호출
        validateTripPlanDays(request.getMinDays(), request.getMaxDays());

        // 사용자 대표 이미지 조회
        UserImage userImage = userImageRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("대표 이미지가 설정되지 않았습니다."));
        String representativeImageUrl = userImage.getImageUrl();

        // 여행 계획 생성
        TripPlan tripPlan = TripPlanConverter.toEntity(request, user, room, representativeImageUrl, room.getName(), tripPlanType);
        tripPlanRepository.save(tripPlan);

        // 사용자 대표 이미지 제거
        userImageRepository.delete(userImage);

        // 방 멤버들에게 투표 연결
        linkTripPlanToRoomMembers(tripPlan, room);

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

    private final List<String> availableImages = List.of(
            "https://example.com/images/1.jpg",
            "https://example.com/images/2.jpg",
            "https://example.com/images/3.jpg",
            "https://example.com/images/4.jpg",
            "https://example.com/images/5.jpg"
    );

    @Transactional(readOnly = true)
    public List<String> getAvailableImages() {
        return availableImages;
    }

    @Transactional
    public void saveUserRepresentativeImage(Long userId, String imageUrl) {
        // 이미지 검증
        if (!availableImages.contains(imageUrl)) {
            throw new BusinessException(IMAGE_NOT_FOUND);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        // 사용자 이미지 저장 또는 업데이트
        UserImage userImage = userImageRepository.findByUserId(userId)
                .orElse(UserImage.builder()
                        .user(user)
                        .imageUrl(imageUrl)
                        .build());

        userImage.updateImageUrl(imageUrl);
        userImageRepository.save(userImage);
    }
}
