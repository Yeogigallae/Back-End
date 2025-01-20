package com.umc.yeogi_gal_lae.api.tripPlan.converter;

import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanRequest;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanResponse;

import java.time.LocalDate;

public class TripPlanConverter {

    public static TripPlan toEntity(TripPlanRequest request) {
        TripPlan.TripPlanBuilder builder = TripPlan.builder()
                .name(request.getName())
                .location(request.getLocation())
                .startDate(request.getStartDate() != null ? LocalDate.parse(request.getStartDate()) : null)
                .endDate(request.getEndDate() != null ? LocalDate.parse(request.getEndDate()) : null)
                .tripPlanType(request.getTripPlanType())
                .voteLimitTime(request.getVoteLimitTime())
                .minDays(request.getMinDays())
                .maxDays(request.getMaxDays())
                .groupId(request.getGroupId());

        switch (request.getTripPlanType()) {
            case SCHEDULE:
                if (request.getScheduleDetails() != null) {
                    builder.description(request.getScheduleDetails().getMessage())
                            .price(request.getScheduleDetails().getPrice());
                }
                break;
            case BUDGET:
                if (request.getBudgetDetails() != null) {
                    builder.transportation(request.getBudgetDetails().getTransportation())
                            .accommodation(request.getBudgetDetails().getAccommodation())
                            .meal(request.getBudgetDetails().getMeal());
                }
                break;
            case COURSE:
                if (request.getCourseDetails() != null) {
                    builder.description(request.getCourseDetails().getMessage());
                }
                break;
        }

        return builder.build();
    }

    public static TripPlanResponse toResponse(TripPlan tripPlan) {
        return TripPlanResponse.builder()
                .id(tripPlan.getId())
                .name(tripPlan.getName())
                .location(tripPlan.getLocation())
                .startDate(tripPlan.getStartDate() != null ? tripPlan.getStartDate().toString() : null)
                .endDate(tripPlan.getEndDate() != null ? tripPlan.getEndDate().toString() : null)
                .tripPlanType(tripPlan.getTripPlanType())
                .voteLimitTime(tripPlan.getVoteLimitTime())
                .price(tripPlan.getPrice())
                .minDays(tripPlan.getMinDays())
                .maxDays(tripPlan.getMaxDays())
                .groupId(tripPlan.getGroupId())
                .transportation(tripPlan.getTransportation())
                .accommodation(tripPlan.getAccommodation())
                .meal(tripPlan.getMeal())
                .description(tripPlan.getDescription())
                .build();
    }
}