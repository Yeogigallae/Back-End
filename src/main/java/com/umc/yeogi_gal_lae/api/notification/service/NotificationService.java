package com.umc.yeogi_gal_lae.api.notification.service;

import com.umc.yeogi_gal_lae.api.notification.dto.NotificationDto;
import com.umc.yeogi_gal_lae.api.notification.domain.Notification;
import com.umc.yeogi_gal_lae.api.notification.domain.NotificationType;
import com.umc.yeogi_gal_lae.api.notification.repository.NotificationRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.types.TripPlanType;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * 시작 알림 생성 (TripPlan 관련 정보 추가)
     */
    @Transactional
    public void createStartNotification(String roomName, String userName, String userEmail, NotificationType type, Long tripPlanId, TripPlanType tripPlanType) {
        saveNotification(roomName, userName, userEmail, type, true, tripPlanId, tripPlanType);
    }

    /**
     * 종료 알림 생성 (TripPlan 관련 정보 추가)
     */
    @Transactional
    public void createEndNotification(String roomName, String userEmail, NotificationType type, Long tripPlanId, TripPlanType tripPlanType) {
        saveNotification(roomName, null, userEmail, type, false, tripPlanId, tripPlanType);
    }


    /**
     * 공통적으로 Notification 객체를 생성하여 저장하는 메서드
     */
    private void saveNotification(String roomName, String userName, String userEmail, NotificationType type, boolean isStart, Long tripPlanId, TripPlanType tripPlanType) {
        Notification notification = new Notification();
        notification.setRoomName(roomName);
        notification.setUserName(userName);
        notification.setUserEmail(userEmail); // 이메일 저장
        notification.setType(type);
        notification.setContent(generateCaption(roomName, userName, type, isStart));
        notification.setTripPlanId(tripPlanId);
        notification.setTripPlanType(tripPlanType);

        notificationRepository.save(notification);
    }

    /**
     * ✅ 특정 유저의 최신 알림 리스트 조회 (타입 필터링 지원)
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(String userEmail) {
        // 🔹 userEmail을 기반으로 User 객체 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 🔹 알림 조회 (필터링이 없으면 모든 알림 조회)
        List<Notification> notifications;
        notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);

        // 🔹 알림이 없으면 목업 데이터 반환
        if (notifications.isEmpty()) {
            log.warn("알림 데이터 없음, 기본 목업 데이터 반환");
            return mockNotifications();
        }

        return notifications.stream()
                .map(notification -> new NotificationDto(
                        notification.getId(),
                        notification.getType().getTitle(),
                        generateCaption(notification.getRoomName(), notification.getUserName(), notification.getType(),
                                isStartNotification(notification.getType())),
                        notification.getType().name(),
                        notification.getTripPlanId(),
                        notification.getTripPlanType()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 목업 데이터 반환 메서드
     */
    private List<NotificationDto> mockNotifications() {
        return List.of(
                new NotificationDto(1L, "투표 시작", "Mock 투표가 시작되었습니다!", "VOTE_START", 100L, TripPlanType.COURSE),
                new NotificationDto(2L, "예산 설정 시작", "Mock 예산 설정이 시작되었습니다!", "BUDGET_START", 101L, TripPlanType.SCHEDULE),
                new NotificationDto(3L, "코스 선택 시작", "Mock 코스 선택이 시작되었습니다!", "COURSE_START", 102L, TripPlanType.SCHEDULE)
        );
    }

    /**
     * 특정 알림 타입이 "시작" 알림인지 체크
     */
    private boolean isStartNotification(NotificationType type) {
        return type == NotificationType.VOTE_START ||
                type == NotificationType.COURSE_START ||
                type == NotificationType.BUDGET_START;
    }

    /**
     * 알림에 대한 캡션 생성 (시작 / 종료 통합)
     */
    private String generateCaption(String roomName, String userName, NotificationType type, boolean isStart) {
        if (isStart) {
            return String.format("%s 방의 %s님이 %s를 시작했습니다!", roomName, userName, type.getKorean());
        } else {
            return String.format("%s 방의 %s 확인해보세요!", roomName, type.getKorean());
        }
    }

    /**
     *  특정 알림 읽음 처리
     */
    @Transactional
    public void markNotificationAsRead(Long notificationId, String userEmail) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        // 현재 로그인한 사용자의 이메일과 알림 수신자의 이메일 비교
        if (!notification.getUserEmail().equals(userEmail)) {
            throw new BusinessException(ErrorCode.FORBIDDEN); // 403 FORBIDDEN
        }

        notification.markAsRead();
        notificationRepository.save(notification);
    }

    /**
     *  특정 사용자의 읽지 않은 알림 개수 조회 (이메일 기반)
     */
    @Transactional(readOnly = true)
    public boolean hasUnreadNotifications(String userEmail) {
        return notificationRepository.countUnreadNotificationsByUser(userEmail) > 0;
    }
}