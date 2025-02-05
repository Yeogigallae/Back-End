package com.umc.yeogi_gal_lae.api.tripPlan.repository;

import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripPlanRepository extends JpaRepository<TripPlan, Long> {
    List<TripPlan> findByRoomId(Long roomId); // 특정 Room의 TripPlan 조회
}