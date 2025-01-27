package com.umc.yeogi_gal_lae.api.friendship.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Data;


@Data
@Entity
@Table(name = "friendship_invite")
public class FriendshipInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long inviterId;          // 초대한 사용자 ID
    private String inviteeEmail;     // 초대받는 사용자의 이메일
    private String token;            // 랜덤하게 생성된 토큰
    private LocalDateTime createdAt; // 초대 생성 시간
}