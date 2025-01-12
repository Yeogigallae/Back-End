package com.umc.yeogi_gal_lae.api.room.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class RoomMemberId implements Serializable {

    @Column(name = "room_id") // 매핑 이름 명시
    private Long roomId;

    @Column(name = "user_id") // 매핑 이름 명시
    private Long userId;

    // 기본 생성자
    public RoomMemberId() {}

    public RoomMemberId(Long roomId, Long userId) {
        this.roomId = roomId;
        this.userId = userId;
    }
}