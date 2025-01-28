package com.umc.yeogi_gal_lae.api.friendship.domain;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User users;

    private Long inviterId;
    private Long inviteeId;
    private FriendshipStatus status;

}