package com.umc.yeogi_gal_lae.domain.room.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "room_member")
public class RoomMember {

    @EmbeddedId
    private RoomMemberId id;

    @MapsId("roomId") // EmbeddedId의 roomId와 매핑
    @ManyToOne
    @JoinColumn(name = "room_id") // room_id로 매핑
    private Room room;

//    @MapsId("userId") // EmbeddedId의 userId와 매핑
//    @ManyToOne
//    @JoinColumn(name = "user_id") // user_id로 매핑
//    private User user;
}