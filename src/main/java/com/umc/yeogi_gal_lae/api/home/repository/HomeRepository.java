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

    // 완료된 투표방과 연관된 종료 날짜가 현재 또는 미래인 여행 계획 조회
    @Query("SELECT tp FROM TripPlan tp JOIN tp.voteRoom vr WHERE vr.tripPlan.status = 'COMPLETED' AND tp.endDate >= CURRENT_DATE")
    List<TripPlan> findCompletedVoteRoomsWithEndDateAfterNow();
}
