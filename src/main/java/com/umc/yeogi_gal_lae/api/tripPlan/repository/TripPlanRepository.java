package com.umc.yeogi_gal_lae.api.tripPlan.repository;

import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TripPlanRepository extends JpaRepository<TripPlan, Long> {
    List<TripPlan> findByRoomId(Long roomId); // 특정 Room의 TripPlan 조회
    void deleteByUser(User user);
    @Modifying
    @Query("UPDATE TripPlan tp SET tp.voteRoom = NULL WHERE tp.user = :user")
    void detachVoteRoomByUser(@Param("user") User user);
  
    Optional<TripPlan> findTripPlanByRoomId(Long roomId);
}