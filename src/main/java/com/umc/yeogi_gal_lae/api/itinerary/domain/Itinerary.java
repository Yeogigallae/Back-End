package com.umc.yeogi_gal_lae.api.itinerary.domain;

import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "itineraries")
public class Itinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itineraryJson;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_plan_id", nullable = false, unique = true)
    private TripPlan tripPlan;
}
