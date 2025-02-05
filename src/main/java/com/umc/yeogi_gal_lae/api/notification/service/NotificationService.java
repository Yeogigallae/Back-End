package com.umc.yeogi_gal_lae.api.notification.service;

import com.umc.yeogi_gal_lae.api.notification.dto.NotificationDto;
import com.umc.yeogi_gal_lae.api.notification.domain.Notification;
import com.umc.yeogi_gal_lae.api.notification.domain.NotificationType;
import com.umc.yeogi_gal_lae.api.notification.repository.NotificationRepository;
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
     * 시작 알림 생성
     */
    @Transactional
    public void createStartNotification(String roomName, String userName, NotificationType type) {
        saveNotification(roomName, userName, type, true);
    }

    /**
     * 종료 알림 생성
     */
    @Transactional
    public void createEndNotification(String roomName, NotificationType type) {
        saveNotification(roomName, null, type, false);
    }

    /**
     * 공통적으로 Notification 객체를 생성하여 저장하는 메서드
     */
    private void saveNotification(String roomName, String userName, NotificationType type, boolean isStart) {
        Notification notification = new Notification();
        notification.setRoomName(roomName);
        notification.setUserName(userName);
        notification.setType(type);
        notification.setContent(generateCaption(roomName, userName, type, isStart));

        notificationRepository.save(notification);
    }

    /**
     * 최신순으로 정렬된 알림 리스트 조회
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAllByOrderByCreatedAtDesc();
        return notifications.stream()
                .map(notification -> new NotificationDto(
                        notification.getId(),
                        notification.getType().getTitle(), // Enum에서 직접 가져오기
                        generateCaption(notification.getRoomName(), notification.getUserName(), notification.getType(),
                                notification.getType() == NotificationType.VOTE_START ||
                                        notification.getType() == NotificationType.COURSE_START ||
                                        notification.getType() == NotificationType.BUDGET_START),
                        notification.getType().name()
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
}