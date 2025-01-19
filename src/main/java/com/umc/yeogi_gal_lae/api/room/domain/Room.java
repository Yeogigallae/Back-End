package com.umc.yeogi_gal_lae.api.room.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Entity
@Table(name = "room")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 초기화하는 생성자
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id") // 명시적으로 컬럼 이름 정의
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Long masterId;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomMember> roomMembers; // RoomMember와의 관계


    public Room(String name, Long masterId) {
        this.name = name;
        this.masterId = masterId;
    }
}