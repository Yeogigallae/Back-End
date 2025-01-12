package com.umc.yeogi_gal_lae.api.vote.domain;


import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @OneToMany(mappedBy = "vote", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<User> users;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tripPlan_id", nullable = false)
    private TripPlan tripPlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType type;
}
