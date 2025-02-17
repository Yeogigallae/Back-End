package com.umc.yeogi_gal_lae.api.home.converter;

import com.umc.yeogi_gal_lae.api.aiCourse.domain.AICourse;
import com.umc.yeogi_gal_lae.api.home.dto.HomeResponse;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteRoom;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class HomeConverter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    public static HomeResponse.OngoingVoteRoom toOngoingVoteRoom(VoteRoom voteRoom) {

        List<String> profileImageUrls = voteRoom.getTripPlan().getRoom().getRoomMembers().stream()
            .map(member -> member.getUser().getProfileImage())
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        return new HomeResponse.OngoingVoteRoom(
                voteRoom.getTripPlan().getId(),
<<<<<<< HEAD
=======
                voteRoom.getTripPlan().getRoom().getId(),
                voteRoom.getTripPlan().getRoom().getMaster().getId(),
>>>>>>> 4b4e58c5ccff6fd9a9455a44a02688d41829b861
                voteRoom.getTripPlan().getRoom().getName(),
                voteRoom.getTripPlan().getLocation(),
                voteRoom.getTripPlan().getRoom().getRoomMembers().size(),
                voteRoom.getTripPlan().getVoteLimitTime(),
                voteRoom.getTripPlan().getRoom().getRoomMembers().stream().filter(m -> m.getUser().getVote() != null).count(),
                profileImageUrls,
                voteRoom.getCreatedAt(),
                voteRoom.getTripPlan().getTripPlanType(),
                voteRoom.getTripPlan().getLatitude(),
                voteRoom.getTripPlan().getLongitude()
                );
    }

    public static HomeResponse.CompletedVoteRoom toCompletedVoteRoom(TripPlan tripPlan, Long aiCourseId) {

        return new HomeResponse.CompletedVoteRoom(
<<<<<<< HEAD
                tripPlan.getId(),
<<<<<<< HEAD
=======
                tripPlan.getRoom().getId(),
>>>>>>> 4b4e58c5ccff6fd9a9455a44a02688d41829b861
                tripPlan.getRoom().getName(),
                tripPlan.getLocation(),
                tripPlan.getStartDate(),
                tripPlan.getEndDate().format(DATE_FORMATTER),
                tripPlan.getImageUrl()
=======
            tripPlan.getId(),
            tripPlan.getRoom().getId(),
            tripPlan.getRoom().getName(),
            aiCourseId,
            tripPlan.getLocation(),
            tripPlan.getStartDate(),
            tripPlan.getEndDate().format(DATE_FORMATTER),
            tripPlan.getImageUrl()
>>>>>>> 1d417f6f749704d04302dc3ce84ad27d7670042d
        );
    }

    public static HomeResponse.CompletedTripPlan toCompletedTripPlan(TripPlan tripPlan) {
        return new HomeResponse.CompletedTripPlan(
                tripPlan.getId(),
                tripPlan.getRoom().getName(),
                tripPlan.getLocation(),
                tripPlan.getStartDate(),
                tripPlan.getEndDate().format(DATE_FORMATTER),
                tripPlan.getTripType(),
                tripPlan.getImageUrl()
        );
    }
}
