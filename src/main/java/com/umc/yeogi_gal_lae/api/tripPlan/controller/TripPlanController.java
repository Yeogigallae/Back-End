package com.umc.yeogi_gal_lae.api.tripPlan.controller;

import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanRequest;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanResponse;
import com.umc.yeogi_gal_lae.api.tripPlan.service.TripPlanService;
import com.umc.yeogi_gal_lae.api.tripPlan.types.TripPlanType;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TripPlanController {

    private final TripPlanService tripPlanService;

    @Operation(summary = "새로운 투표 생성", description = "TripPlanType에 따라 새로운 투표를 생성합니다.")
    @PostMapping("/trip-plan/votes/{tripPlanType}")
    public Response<TripPlanResponse> createVote(
            @PathVariable TripPlanType tripPlanType,
            @RequestBody TripPlanRequest request) {

        TripPlanResponse response = tripPlanService.createTripPlan(request, request.getUserId(), request.getRoomId(), tripPlanType);
        return Response.of(SuccessCode.OK, response);
    }

    @Operation(summary = "투표 생성 대표이미지 선택", description = "투표 생성 대표이미지를 선택 API입니다.")
    @PostMapping("/images/select")
    public Response<String> saveRepresentativeImage(
            @RequestParam Long userId,
            @RequestParam String imageUrl) {
        tripPlanService.saveUserRepresentativeImage(userId, imageUrl);
        return Response.of(SuccessCode.OK, "대표 이미지가 저장되었습니다.");
    }

    @Operation(summary = "투표 생성 대표이미지 조회", description = "투표 생성 대표이미지를 조회 API입니다.")
    @GetMapping("/images")
    public Response<List<String>> getAvailableImages() {
        List<String> images = tripPlanService.getAvailableImages();
        return Response.of(SuccessCode.OK, images);
    }

    @Operation(summary = "여행 계획 상세 조회", description = "특정 여행 계획의 상세 정보를 반환합니다.")
    @GetMapping("/trip-plan/{tripPlanId}/details")
    public Response<TripPlanResponse> getTripPlanDetails(@PathVariable Long tripPlanId) {
        TripPlanResponse response = tripPlanService.getTripPlanDetails(tripPlanId);
        return Response.of(SuccessCode.OK, response);
    }
}
