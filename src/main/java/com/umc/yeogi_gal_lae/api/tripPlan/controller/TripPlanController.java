package com.umc.yeogi_gal_lae.api.tripPlan.controller;

import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanRequest;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanResponse;
import com.umc.yeogi_gal_lae.api.tripPlan.service.TripPlanService;
import com.umc.yeogi_gal_lae.api.tripPlan.types.TripPlanType;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TripPlanController {

    private final TripPlanService tripPlanService;

    @Operation(summary = "새로운 여행 계획 생성", description = "사용자가 직접 이미지 URL과 계획들을 입력하여 여행 계획을 생성합니다.")
    @PostMapping("/trip-plan/votes/{tripPlanType}")
    public Response<TripPlanResponse> createVote(
        @PathVariable TripPlanType tripPlanType,
        @RequestBody TripPlanRequest request) {

        TripPlanResponse response = tripPlanService.createTripPlan(request, request.getUserId(), request.getRoomId(), tripPlanType);
        return Response.of(SuccessCode.TRIP_PLAN_CREATED_OK, response);
    }

    @Operation(summary = "여행 계획 상세 조회", description = "특정 여행 계획의 상세 정보를 반환합니다.")
    @GetMapping("/trip-plan/{tripPlanId}/details")
    public Response<TripPlanResponse> getTripPlanDetails(@PathVariable Long tripPlanId) {
        TripPlanResponse response = tripPlanService.getTripPlanDetails(tripPlanId);
        return Response.of(SuccessCode.TRIP_PLAN_RESULT_OK, response);
    }
}
