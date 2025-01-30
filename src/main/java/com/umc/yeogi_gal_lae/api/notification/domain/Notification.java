package com.umc.yeogi_gal_lae.api.notification.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomName; // 방 이름
    private String userName; // 사용자 이름 (시작 알림에만 필요)

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String content; // 알림 내용

    @CreationTimestamp
    private LocalDateTime createdAt;
}