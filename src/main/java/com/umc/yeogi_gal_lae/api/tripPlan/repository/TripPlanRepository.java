package com.umc.yeogi_gal_lae.api.tripPlan.repository;

import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripPlanRepository extends JpaRepository<TripPlan, Long> {

}