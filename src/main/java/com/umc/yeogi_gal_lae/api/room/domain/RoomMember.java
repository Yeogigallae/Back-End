package com.umc.yeogi_gal_lae.api.room.domain;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "room_member")
public class RoomMember {

    @EmbeddedId
    private RoomMemberId id;

    @MapsId("roomId") // EmbeddedId의 roomId와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false) // 중복 매핑 방지
    private Room room;

    @MapsId("userId") // RoomMemberId의 userId와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false) // 중복 매핑 방지
    private User user;
//    @MapsId("userId") // EmbeddedId의 userId와 매핑
//    @ManyToOne
//    @JoinColumn(name = "user_id") // user_id로 매핑
//    private User user;
}