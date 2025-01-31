package com.umc.yeogi_gal_lae.api.tripPlan.domain;

import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.tripPlan.types.*;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteRoom;
import com.umc.yeogi_gal_lae.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="trip_plans")
public class TripPlan extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column
    private String description;

    @Column
    private String price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripType tripType; // ENUM: 국내/해외

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripPlanType tripPlanType; // COURSE, SCHEDULE, BUDGET (여행 계획 생성, 여행 코스 생성, 여행 자본 생성)

    @Enumerated(EnumType.STRING)
    @Column
    private VoteLimitTime voteLimitTime;

//    @Enumerated(EnumType.STRING)
//    @Column
//    private Accommodation accommodation;
//
//    @Enumerated(EnumType.STRING)
//    @Column
//    private Meal meal;
//
//    @Enumerated(EnumType.STRING)
//    @Column
//    private Transportation transportation;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.PLANNED;     // 기본값 설정


    @Column(nullable = false, length = 50)
    private String location;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column
    private Integer minDays; // 최소 숙박일

    @Column
    private Integer maxDays; // 최대 숙박일

    @Column(name = "group_name") // DB 컬럼 이름 변경
    private String groupName; // 필드명 변경

    @Column
    private String imageUrl; // **이미지 URL 추가**

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 여행 계획 작성자

    // 방과의 연관 관계 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false) // 방 ID를 외래 키로 설정
    private Room room; // 여행 계획이 속한 방

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "vote_room_id", nullable = true)
    private VoteRoom voteRoom;


    // 자동 동기
    public void setVoteRoom(VoteRoom voteRoom) {
        if (this.voteRoom != voteRoom) { // 현재 상태를 확인
            this.voteRoom = voteRoom;
            if (voteRoom != null) {
                voteRoom.setTripPlan(this); // 순환 호출 방지
            }
        }
    }


}
