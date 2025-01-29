package com.umc.yeogi_gal_lae.api.notification.dto;

import com.umc.yeogi_gal_lae.api.notification.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationDto {
    private NotificationType type; // 알림 종류
    private String content;        // 알림 내용
    private Boolean isRead;        // 읽음 여부
    private Long userId;           // 유저 ID
    private Long roomId;           // 방 ID
}