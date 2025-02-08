//package com.umc.yeogi_gal_lae.api.aiTripPlan.controller;
//
//import com.umc.yeogi_gal_lae.api.aiTripPlan.dto.response.AITripPlanResponse;
//import com.umc.yeogi_gal_lae.api.aiTripPlan.service.AITripPlanService;
//import com.umc.yeogi_gal_lae.global.common.response.Response;
//import com.umc.yeogi_gal_lae.global.success.SuccessCode;
//import io.swagger.v3.oas.annotations.Operation;
//import jakarta.validation.constraints.Min;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/rooms")
//@Validated // 유효성 검사 활성화
//public class AITripPlanController {
//
//    private final AITripPlanService aiTripPlanService;
//
//    public AITripPlanController(AITripPlanService aiTripPlanService) {
//        this.aiTripPlanService = aiTripPlanService;
//    }
//
//    /**
//     * 특정 Room에 속한 모든 Place를 기반으로 여행 일정 생성
//     *
//     * @param roomId Room의 ID
//     * @return 생성된 여행 일정
//     */
//    @Operation(summary = "특정 Room의 모든 장소를 기반으로 여행 일정 생성")
//    @GetMapping("/{roomId}/itinerary")
//    public Response<AITripPlanResponse> generateTripPlan(
//            @PathVariable @Min(value = 1, message = "roomId는 1 이상이어야 합니다.") Long roomId) {
//        AITripPlanResponse response = aiTripPlanService.generateTripPlan(roomId);
//
//        return Response.of(SuccessCode.OK, response);
//    }
//}