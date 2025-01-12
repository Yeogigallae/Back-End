package com.umc.yeogi_gal_lae.api.notification.controller;

import com.umc.yeogi_gal_lae.api.notification.domain.Notification;
import com.umc.yeogi_gal_lae.api.notification.dto.CreateNotificationRequest;
import com.umc.yeogi_gal_lae.api.notification.dto.NotificationDto;
import com.umc.yeogi_gal_lae.api.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 알림 생성 API (POST)
     */
    @PostMapping
    public ResponseEntity<Long> createNotification(@RequestBody @Valid CreateNotificationRequest request) {
        Long notificationId = notificationService.createNotification(request);
        return ResponseEntity.ok(notificationId);
    }

    /**
     * 알림 조회 API (GET)
     */
    @GetMapping
//    public ResponseEntity<List<NotificationDto>> getNotifications(@RequestParam Long userId) {
////        List<NotificationDto> notifications = notificationService.getAllNotifications(userId);
////        return ResponseEntity.ok(notifications);
//    }

    /**
     * 개별 알림 읽음 처리 API (PUT)
     */
    @PutMapping("/{notificationId}")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
//        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    /**
     * 모든 알림 읽음 처리 API (PUT)
     */
    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllNotificationsAsRead(@RequestParam Long userId) {
//        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}