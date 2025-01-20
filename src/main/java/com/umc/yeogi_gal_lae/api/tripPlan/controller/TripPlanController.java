package com.umc.yeogi_gal_lae.api.tripPlan.controller;

import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanRequest;
import com.umc.yeogi_gal_lae.api.tripPlan.dto.TripPlanResponse;
import com.umc.yeogi_gal_lae.api.tripPlan.service.TripPlanService;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TripPlanController {

    private final TripPlanService tripPlanService;

    @Operation(summary = "새로운 투표 생성", description = "새로운 투표를 생성합니다.")
    @PostMapping("/trip-plan/votes")
    public Response<TripPlanResponse> createVote(@RequestBody TripPlanRequest request) {
        TripPlanResponse response = tripPlanService.createTripPlan(request);
        return Response.of(SuccessCode.OK, response);
    }

    @Operation(summary = "투표에 이미지 업로드", description = "특정 투표에 이미지를 업로드합니다.")
    @PostMapping("/trip-plan/votes/{voteId}/image")
    public Response<String> uploadVoteImage(@PathVariable Long voteId) {
        // 이미지 업로드 로직 구현
        return Response.of(SuccessCode.OK, "이미지 업로드 완료");
    }

    @Operation(summary = "투표 공유", description = "특정 투표를 방에 공유합니다.")
    @PostMapping("/trip-plan/votes/{voteId}/share/room/{roomId}")
    public Response<String> shareVote(@PathVariable Long voteId, @PathVariable Long roomId) {
        // 투표 공유 로직 구현
        return Response.of(SuccessCode.OK, "투표 공유 완료");
    }

    @Operation(summary = "예산 데이터 저장", description = "사용자가 입력한 예산 데이터를 저장합니다.")
    @PostMapping("/trip-plan/budget")
    public Response<String> saveBudget(@RequestBody String budgetRequest) {
        // 예산 저장 로직 구현
        return Response.of(SuccessCode.OK, "예산 저장 완료");
    }

    @Operation(summary = "예산 데이터 조회", description = "특정 예산 데이터를 조회합니다.")
    @GetMapping("/trip-plan/budget/{budgetId}")
    public Response<String> getBudget(@PathVariable Long budgetId) {
        // 예산 조회 로직 구현
        return Response.of(SuccessCode.OK, "예산 데이터 조회 완료");
    }
}
