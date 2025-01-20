package com.umc.yeogi_gal_lae.api.tripPlan.service;

import com.umc.yeogi_gal_lae.api.tripPlan.converter.TripPlanConverter;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanRequest;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanResponse;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TripPlanService {
    private final TripPlanRepository tripPlanRepository;

    @Transactional
    public TripPlanResponse createTripPlan(TripPlanRequest request) {
        switch (request.getTripPlanType()) {
            case COURSE:
                return createCourseVote(request);
            case SCHEDULE:
                return createScheduleVote(request);
            case BUDGET:
                return createBudgetVote(request);
            default:
                throw new IllegalArgumentException("일치하는 여행 계획 유형이 없습니다.");
        }
    }

    private TripPlanResponse createCourseVote(TripPlanRequest request) {
        TripPlan tripPlan = TripPlanConverter.toEntity(request);
        tripPlanRepository.save(tripPlan);
        return TripPlanConverter.toResponse(tripPlan);
    }

    private TripPlanResponse createScheduleVote(TripPlanRequest request) {
        TripPlan tripPlan = TripPlanConverter.toEntity(request);
        tripPlanRepository.save(tripPlan);
        return TripPlanConverter.toResponse(tripPlan);
    }

    private TripPlanResponse createBudgetVote(TripPlanRequest request) {
        TripPlan tripPlan = TripPlanConverter.toEntity(request);
        tripPlanRepository.save(tripPlan);
        return TripPlanConverter.toResponse(tripPlan);
    }

}
