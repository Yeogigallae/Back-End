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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "trip_plan_id", nullable = false)
    private TripPlan tripPlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType type;
}
