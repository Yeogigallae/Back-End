package com.umc.yeogi_gal_lae.api.notification.controller;

import com.umc.yeogi_gal_lae.api.notification.dto.NotificationDto;
import com.umc.yeogi_gal_lae.api.notification.service.NotificationService;
import com.umc.yeogi_gal_lae.api.notification.domain.NotificationType;
import com.umc.yeogi_gal_lae.api.vote.AuthenticatedUserUtils;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
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
     * 시작 알림 생성 - 테스트용
     */
    @PostMapping("/start")
    public ResponseEntity<Response<Void>> createStartNotification(
            @RequestParam String roomName,
            @RequestParam String userName,
            @RequestParam String userEmail,
            @RequestParam NotificationType type) {
        notificationService.createStartNotification(roomName, userName, userEmail, type);
        return ResponseEntity.ok(Response.of(SuccessCode.NOTIFICATION_START_OK));
    }

    /**
     * 종료 알림 생성 - 테스트용
     */
    @PostMapping("/end")
    public ResponseEntity<Response<Void>> createEndNotification(
            @RequestParam String roomName,
            @RequestParam String userEmail,
            @RequestParam NotificationType type) {
        notificationService.createEndNotification(roomName, userEmail, type);
        return ResponseEntity.ok(Response.of(SuccessCode.NOTIFICATION_END_OK));
    }

    /**
     * 최신 알림 조회 API
     */
    @GetMapping
    public ResponseEntity<Response<List<NotificationDto>>> getAllNotifications() {
        List<NotificationDto> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(Response.of(SuccessCode.NOTIFICATION_FETCH_OK, notifications));
    }

    /**
     * 특정 알림 읽음 처리
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<Response<Void>> markNotificationAsRead(
        @PathVariable Long id) {
        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();
        notificationService.markNotificationAsRead(id, userEmail);
        return ResponseEntity.ok(Response.of(SuccessCode.NOTIFICATION_READ_OK));
    }
}