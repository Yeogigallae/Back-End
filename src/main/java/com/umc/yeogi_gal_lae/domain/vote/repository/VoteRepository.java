package com.umc.yeogi_gal_lae.domain.vote.repository;

import com.umc.yeogi_gal_lae.domain.vote.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {

    @Query("SELECT v FROM Vote v WHERE v.user.id = :userId AND v.tripPlan.id = :tripPlanId")
    Vote findVoteByUserAndTripPlan(@Param("userId") Long userId, @Param("tripPlanId") Long tripPlanId);
}
