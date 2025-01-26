package com.umc.yeogi_gal_lae.api.vote.repository;

import com.umc.yeogi_gal_lae.api.vote.domain.VoteRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRoomRepository extends JpaRepository<VoteRoom,Long> {

    Optional<VoteRoom> findVoteRoomByTripPlanId(Long tripId);
}
