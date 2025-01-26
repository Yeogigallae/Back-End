package com.umc.yeogi_gal_lae.api.vote.domain;


import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter  @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="vote")
@Entity
public class Vote extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "trip_plan_id", nullable = false)
    private TripPlan tripPlan;

    // 특정 사용자의 투표 결과 조회 빈번하게 일어날 것으로 예상되기에, VoteRoom 을 통하지 않고 바로 Use 와 매핑
    // VoteRoom 은 투표 방 자체를 관리하고, Vote 는 사용자들의 실제 투표 데이터를 관리하므로 역할 분리
    // 투표 데이터를 추가하거나 변경할 때 VoteRoom 에 불필요한 데이터가 섞이지 않기 위함

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "vote_room_id", nullable = false)
    private VoteRoom voteRoom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType type;
}
