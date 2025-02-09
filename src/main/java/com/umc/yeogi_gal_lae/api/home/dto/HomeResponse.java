package com.umc.yeogi_gal_lae.api.home.dto;

import com.umc.yeogi_gal_lae.api.tripPlan.types.TripPlanType;
import com.umc.yeogi_gal_lae.api.tripPlan.types.TripType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class HomeResponse {

    @Getter
    @AllArgsConstructor
    public static class OngoingVoteRoom {
        private String roomName;
        private String location;
        private int totalMembers;
        private String remainingTime;
        private long completedVotes;
        private List<String> profileImageUrls;
        private LocalDateTime createdAt;
        private TripPlanType tripPlanType;
    }

    @Getter
    @AllArgsConstructor
    public static class CompletedVoteRoom {
        private String roomName;
        private String location;
        private LocalDate startDate;
        private String endDate;
        private String imageUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class CompletedTripPlan {
        private String roomName;
        private String location;
        private LocalDate startDate;
        private String endDate;
        private TripType tripType;
        private String imageUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class OngoingVoteRoomList {
        private int totalCount;
        private List<OngoingVoteRoom> rooms;
    }

    @Getter
    @AllArgsConstructor
    public static class CompletedVoteRoomList {
        private int totalCount;
        private List<CompletedVoteRoom> rooms;
    }

    @Getter
    @AllArgsConstructor
    public static class CompletedTripPlanList {
        private int totalCount;
        private List<CompletedTripPlan> trips;
    }

    @Getter
    @AllArgsConstructor
    public static class NotificationStatus {
        private boolean hasUnreadNotifications;
    }
}
