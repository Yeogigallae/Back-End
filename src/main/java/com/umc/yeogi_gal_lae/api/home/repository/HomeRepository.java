package com.umc.yeogi_gal_lae.api.home.repository;

import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.types.Status;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HomeRepository extends JpaRepository<TripPlan, Long> {
    List<TripPlan> findByStatus(Status status);

    @Query("SELECT vr FROM VoteRoom vr JOIN vr.tripPlan tp WHERE tp.status = 'ONGOING'")
    List<VoteRoom> findAllOngoingVoteRooms();
}
