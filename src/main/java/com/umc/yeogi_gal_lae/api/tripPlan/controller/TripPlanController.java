package com.umc.yeogi_gal_lae.api.tripPlan.controller;

import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanRequest;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanResponse;
import com.umc.yeogi_gal_lae.api.tripPlan.service.TripPlanService;
import com.umc.yeogi_gal_lae.api.tripPlan.types.TripPlanType;
import com.umc.yeogi_gal_lae.api.user.domain.User;
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
            @PathVariable TripPlanType tripPlanType, // TripPlanType을 PathVariable로 받음
            @RequestBody TripPlanRequest request) {

        // 요청 객체의 TripPlanType을 제거하고 PathVariable 값을 사용
        request.setTripPlanType(tripPlanType);

        TripPlanResponse response = tripPlanService.createTripPlan(request, request.getUserId());
        return Response.of(SuccessCode.OK, response);
    }

    @Operation(summary = "이미지 리스트 반환", description = "사용 가능한 이미지 리스트를 반환합니다.")
    @GetMapping("/trip-plan/votes/images")
    public Response<List<String>> getAvailableImages() {
        List<String> images = tripPlanService.getAvailableImages();
        return Response.of(SuccessCode.OK, images);
    }

    @Operation(summary = "특정 여행 계획 공유", description = "특정 TripPlanId에 해당하는 데이터를 방(RoomId)에 반환합니다.")
    @GetMapping("/trip-plan/votes/{tripPlanId}/share/room/{roomId}")
    public Response<Map<String, Object>> shareTripPlanToRoom(
            @PathVariable Long tripPlanId,
            @PathVariable Long roomId) {
        Map<String, Object> sharedTripPlanData = tripPlanService.getTripPlanForRoom(tripPlanId, roomId);
        return Response.of(SuccessCode.OK, sharedTripPlanData);
    }

//    @Operation(summary = "예산 데이터 저장", description = "생성된 투표에 예산 데이터를 저장합니다.")
//    @PostMapping("/trip-plan/votes/{voteId}/budget")
//    public Response<String> saveBudget(@PathVariable Long voteId, @RequestBody TripPlanRequest.BudgetDetails budgetDetails) {
//        tripPlanService.saveBudget(voteId, budgetDetails);
//        return Response.of(SuccessCode.OK, "예산 데이터가 성공적으로 저장되었습니다.");
//    }
//
//    @Operation(summary = "예산 데이터 조회", description = "특정 투표의 예산 데이터를 조회합니다.")
//    @GetMapping("/trip-plan/votes/{voteId}/budget")
//    public Response<TripPlanResponse> getBudget(@PathVariable Long voteId) {
//        TripPlanResponse response = tripPlanService.getBudget(voteId);
//        return Response.of(SuccessCode.OK, response);
//    }
}
