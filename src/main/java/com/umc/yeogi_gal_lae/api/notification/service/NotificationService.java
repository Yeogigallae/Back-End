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
        Notification notification = new Notification();
        notification.setRoomName(roomName);
        notification.setUserName(userName);
        notification.setType(type);
        notification.setContent(generateCaptionForStart(roomName, userName, type));
        notificationRepository.save(notification);
    }

    /**
     * 종료 알림 생성
     */
    @Transactional
    public void createEndNotification(String roomName, NotificationType type) {
        Notification notification = new Notification();
        notification.setRoomName(roomName);
        notification.setType(type);
        notification.setContent(generateCaptionForEnd(roomName, type));
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
                        generateTitle(notification),
                        (notification.getType() == NotificationType.VOTE_START ||
                                notification.getType() == NotificationType.COURSE_START ||
                                notification.getType() == NotificationType.BUDGET_START)
                                ? generateCaptionForStart(notification.getRoomName(), notification.getUserName(), notification.getType())
                                : generateCaptionForEnd(notification.getRoomName(), notification.getType()),
                        notification.getType().name()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 알림 타입에 따라 타이틀 생성
     */
    private String generateTitle(Notification notification) {
        switch (notification.getType()) {
            case VOTE_COMPLETE:
                return "투표 종료";
            case VOTE_START:
                return "투표 시작";
            case COURSE_COMPLETE:
                return "코스짜기 완료";
            case COURSE_START:
                return "코스짜기 시작";
            case BUDGET_COMPLETE:
                return "예산정하기 완료";
            case BUDGET_START:
                return "예산정하기 시작";
            default:
                return "알림";
        }
    }

    /**
     * 시작 알림에 대한 캡션 생성
     */
    private String generateCaptionForStart(String roomName, String userName, NotificationType type) {
        return String.format("%s 방의 %s님이 %s를 시작했습니다!", roomName, userName, typeToKorean(type));
    }

    /**
     * 종료 알림에 대한 캡션 생성
     */
    private String generateCaptionForEnd(String roomName, NotificationType type) {
        return String.format("%s 방의 %s 확인해보세요!", roomName, typeToKorean(type));
    }

    /**
     * 타입을 한글로 변환
     */
    private String typeToKorean(NotificationType type) {
        switch (type) {
            case VOTE_COMPLETE:
                return "투표결과를";
            case VOTE_START:
                return "투표";
            case COURSE_COMPLETE:
                return "생성된 코스를";
            case COURSE_START:
                return "코스짜기";
            case BUDGET_COMPLETE:
                return "예산을";
            case BUDGET_START:
                return "예산 정하기";
            default:
                return "알림";
        }
    }
}