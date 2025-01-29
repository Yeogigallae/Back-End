package com.umc.yeogi_gal_lae.api.notification.service;

import com.umc.yeogi_gal_lae.api.notification.dto.CreateNotificationRequest;
import com.umc.yeogi_gal_lae.api.notification.dto.NotificationDto;
import com.umc.yeogi_gal_lae.api.notification.domain.Notification;
import com.umc.yeogi_gal_lae.api.notification.domain.NotificationType;
import com.umc.yeogi_gal_lae.api.notification.repository.NotificationRepository;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;



    // 알림 생성
    @Transactional
    public Long createNotification(CreateNotificationRequest request) {
        // 사용자 및 방 정보 조회
//        User user = userRepository.findById(request.getTargetUserId())
//                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getTargetUserId()));
//        Room room = roomRepository.findById(request.getRoomId())
//                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + request.getRoomId()));

        // 알림 생성
        Notification notification = new Notification();
        notification.setType(request.getType()); // Enum 설정
        notification.setContent(request.getType().getMessage());
//        notification.setUser(user);
//        notification.setRoom(room);
        notification.setIsRead(false); // 읽음 여부 초기화

        // 알림 저장
        notificationRepository.save(notification);
        return notification.getId();
    }



    // 모든 알림 조회
    @Transactional(readOnly = true)
    public List<NotificationDto> getAllNotifications() {
        // 최신순으로 정렬된 알림 데이터 조회
        List<Notification> notifications = notificationRepository.findAllByOrderByIdDesc();

        // Notification 엔티티를 NotificationDto로 변환
        return notifications.stream()
                .map(n -> new NotificationDto(
                        n.getType(),
                        n.getContent(),
                        n.getIsRead(),
                        n.getUser().getId(),
                        n.getRoom().getId()))
                .collect(Collectors.toList());
    }
}