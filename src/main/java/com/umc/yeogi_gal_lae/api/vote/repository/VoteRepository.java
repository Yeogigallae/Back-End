package com.umc.yeogi_gal_lae.api.vote.repository;

import com.umc.yeogi_gal_lae.api.vote.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface VoteRepository extends JpaRepository<Vote,Long> {

    @Query("SELECT v FROM Vote v WHERE v.tripPlan.id = :tripPlanId")
    Optional<Vote> findByTripPlanId(@Param("tripPlanId") Long tripPlanId);

    @Query("SELECT v FROM Vote v JOIN v.users u WHERE u.id = :userId AND v.tripPlan.id = :tripPlanId")
    List<Vote> findVoteByUserAndTripPlan(@Param("userId") Long userId, @Param("tripPlanId") Long tripPlanId);

}
