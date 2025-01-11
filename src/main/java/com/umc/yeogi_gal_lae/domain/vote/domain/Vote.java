package com.umc.yeogi_gal_lae.domain.vote.domain;


import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter  @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="vote")
@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne(fetch = FetchType.LAZY) // User 와 N:1 관계
    // @JoinColumn(name = "user_id", nullable = false) // User 테이블의 id와 매핑
    // private User user;

    // @OneToOne(fetch = FetchType.LAZY) // TripPlan 과 1:1 관계
    // @JoinColumn(name = "trip_plan_id", nullable = false) // TripPlan 테이블의 id와 매핑
    // private TripPlan tripPlan;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType type;       // 투표 타입

    public enum VoteType {
        GOOD, BAD
    }

}
