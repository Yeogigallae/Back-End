package com.umc.yeogi_gal_lae.api.home.converter;

import com.umc.yeogi_gal_lae.api.home.dto.HomeResponse;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteRoom;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HomeConverter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    public static HomeResponse.OngoingVoteRoom toOngoingVoteRoom(VoteRoom voteRoom) {
        Duration duration = Duration.between(LocalDateTime.now(),
                voteRoom.getCreatedAt().plusSeconds(voteRoom.getTripPlan().getVoteLimitTime().getSeconds()));

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        List<String> profileImageUrls = voteRoom.getTripPlan().getRoom().getRoomMembers().stream()
            .map(member -> member.getUser().getProfileImage())
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        return new HomeResponse.OngoingVoteRoom(
                voteRoom.getTripPlan().getRoom().getName(),
                voteRoom.getTripPlan().getLocation(),
                voteRoom.getTripPlan().getRoom().getRoomMembers().size(),
                formattedTime,
                voteRoom.getTripPlan().getRoom().getRoomMembers().stream().filter(m -> m.getUser().getVote() != null).count(),
                profileImageUrls,
                voteRoom.getCreatedAt(),
                voteRoom.getTripPlan().getTripPlanType()
        );
    }

    public static HomeResponse.CompletedVoteRoom toCompletedVoteRoom(TripPlan tripPlan) {
        return new HomeResponse.CompletedVoteRoom(
                tripPlan.getRoom().getName(),
                tripPlan.getLocation(),
                tripPlan.getStartDate(),
                tripPlan.getEndDate().format(DATE_FORMATTER),
                tripPlan.getImageUrl()
        );
    }

    public static HomeResponse.CompletedTripPlan toCompletedTripPlan(TripPlan tripPlan) {
        return new HomeResponse.CompletedTripPlan(
                tripPlan.getRoom().getName(),
                tripPlan.getLocation(),
                tripPlan.getStartDate(),
                tripPlan.getEndDate().format(DATE_FORMATTER),
                tripPlan.getTripType(),
                tripPlan.getImageUrl()
        );
    }
}
