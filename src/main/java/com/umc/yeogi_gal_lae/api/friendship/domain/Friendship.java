package com.umc.yeogi_gal_lae.api.friendship.domain;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "friendship")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 친구 관계 ID

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 요청을 보낸 사용자

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend; // 요청을 받은 사용자
}