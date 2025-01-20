package com.umc.yeogi_gal_lae.api.tripPlan.domain;

import com.umc.yeogi_gal_lae.api.tripPlan.types.*;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.vote.domain.Vote;
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

    private String price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripType tripType; // ENUM: 국내/해외

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripPlanType tripPlanType; // COURSE, SCHEDULE, BUDGET (여행 계획 생성, 여행 코스 생성, 여행 자본 생성)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteLimitTime voteLimitTime;

    @Enumerated(EnumType.STRING)
    @Column
    private Accommodation accommodation;

    @Enumerated(EnumType.STRING)
    @Column
    private Meal meal;

    @Enumerated(EnumType.STRING)
    @Column
    private Transportation transportation;

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

    @Column
    private String groupId; // 그룹 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "tripPlan", fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    private Vote vote;
}
