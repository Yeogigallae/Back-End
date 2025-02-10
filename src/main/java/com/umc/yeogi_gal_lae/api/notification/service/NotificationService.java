package com.umc.yeogi_gal_lae.api.notification.service;

import com.umc.yeogi_gal_lae.api.notification.dto.NotificationDto;
import com.umc.yeogi_gal_lae.api.notification.domain.Notification;
import com.umc.yeogi_gal_lae.api.notification.domain.NotificationType;
import com.umc.yeogi_gal_lae.api.notification.repository.NotificationRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.types.TripPlanType;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

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
     * 최신순으로 정렬된 알림 리스트 조회 (TripPlan 정보 포함)
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAllByOrderByCreatedAtDesc();
        return notifications.stream()
                .map(notification -> new NotificationDto(
                        notification.getId(),
                        notification.getType().getTitle(),
                        generateCaption(notification.getRoomName(), notification.getUserName(), notification.getType(),
                                notification.getType() == NotificationType.VOTE_START ||
                                        notification.getType() == NotificationType.COURSE_START ||
                                        notification.getType() == NotificationType.BUDGET_START),
                        notification.getType().name(),
                        notification.getTripPlanId(),  // ✅ TripPlan ID 추가
                        notification.getTripPlanType() // ✅ TripPlan Type 추가
                ))
                .collect(Collectors.toList());
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