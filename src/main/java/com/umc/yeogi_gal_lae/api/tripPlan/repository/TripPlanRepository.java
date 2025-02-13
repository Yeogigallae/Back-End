package com.umc.yeogi_gal_lae.api.tripPlan.repository;

import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripPlanRepository extends JpaRepository<TripPlan, Long> {
    List<TripPlan> findByRoomId(Long roomId); // 특정 Room의 TripPlan 조회

    Optional<TripPlan> findTripPlanByRoomId(Long roomId);
}