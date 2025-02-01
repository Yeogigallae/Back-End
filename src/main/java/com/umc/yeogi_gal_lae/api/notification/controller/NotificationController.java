package com.umc.yeogi_gal_lae.api.notification.controller;

import com.umc.yeogi_gal_lae.api.notification.dto.NotificationDto;
import com.umc.yeogi_gal_lae.api.notification.service.NotificationService;
import com.umc.yeogi_gal_lae.api.notification.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

//    /**
//     * 시작 알림 생성- 테스트용
//     */
//    @PostMapping("/start")
//    public ResponseEntity<String> createStartNotification(
//            @RequestParam String roomName,
//            @RequestParam String userName,
//            @RequestParam NotificationType type) {
//        notificationService.createStartNotification(roomName, userName, type);
//        return ResponseEntity.ok("시작 알림이 생성되었습니다.");
//    }
//
//    /**
//     * 종료 알림 생성- 테스트용
//     */
//    @PostMapping("/end")
//    public ResponseEntity<String> createEndNotification(
//            @RequestParam String roomName,
//            @RequestParam NotificationType type) {
//        notificationService.createEndNotification(roomName, type);
//        return ResponseEntity.ok("종료 알림이 생성되었습니다.");
//    }

    /**
     * 최신 알림 조회 API
     */
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAllNotifications() {
        List<NotificationDto> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }
}