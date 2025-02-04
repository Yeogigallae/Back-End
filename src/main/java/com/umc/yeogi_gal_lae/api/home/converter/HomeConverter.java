package com.umc.yeogi_gal_lae.api.home.converter;

import com.umc.yeogi_gal_lae.api.home.dto.HomeResponse;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteRoom;

import java.time.Duration;
import java.time.LocalDateTime;

public class HomeConverter {
    public static HomeResponse.OngoingVoteRoom toOngoingVoteRoom(VoteRoom voteRoom) {
        Duration duration = Duration.between(LocalDateTime.now(),
                voteRoom.getCreatedAt().plusSeconds(voteRoom.getTripPlan().getVoteLimitTime().getSeconds()));

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return new HomeResponse.OngoingVoteRoom(
                voteRoom.getTripPlan().getRoom().getName(),
                voteRoom.getTripPlan().getLocation(),
                voteRoom.getTripPlan().getRoom().getRoomMembers().size(),
                formattedTime,
                voteRoom.getTripPlan().getRoom().getRoomMembers().stream().filter(m -> m.getUser().getVote() != null).count()
        );
    }

    public static HomeResponse.CompletedVoteRoom toCompletedVoteRoom(TripPlan tripPlan) {
        return new HomeResponse.CompletedVoteRoom(
                tripPlan.getRoom().getName(),
                tripPlan.getLocation(),
                tripPlan.getStartDate(),
                tripPlan.getEndDate()
        );
    }

    public static HomeResponse.CompletedTripPlan toCompletedTripPlan(TripPlan tripPlan) {
        return new HomeResponse.CompletedTripPlan(
                tripPlan.getName(),
                tripPlan.getStartDate(),
                tripPlan.getEndDate(),
                tripPlan.getTripType() // 여행 유형 포함
        );
    }
}
