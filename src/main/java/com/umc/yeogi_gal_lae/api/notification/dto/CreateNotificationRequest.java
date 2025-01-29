package com.umc.yeogi_gal_lae.api.notification.dto;

import com.umc.yeogi_gal_lae.api.notification.domain.NotificationType;
import lombok.Data;

@Data
public class CreateNotificationRequest {
    private NotificationType type; // 알림 유형 (예: VOTE_START)
    private String username; // 방에서 알림을 보낸 유저 이름
    private Long userId; // 알림 대상 유저 ID
    private Long roomId; // 관련된 방 ID
}