package com.umc.yeogi_gal_lae.api.notification.domain;

import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 알림 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type; // 알림 종류 (Enum)

    @Column(nullable = false, length = 255)
    private String content; // 알림 내용

    @Column(nullable = false)
    private Boolean isRead; // 읽음 여부

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 알림 대상 유저

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room; // 방 정보
}