package com.umc.yeogi_gal_lae.api.aiCourse.controller;

import com.umc.yeogi_gal_lae.api.aiCourse.converter.AICourseConverter;
import com.umc.yeogi_gal_lae.api.aiCourse.domain.AICourse;
import com.umc.yeogi_gal_lae.api.aiCourse.dto.AICourseResponse;
import com.umc.yeogi_gal_lae.api.aiCourse.dto.DailyItineraryResponse;
import com.umc.yeogi_gal_lae.api.aiCourse.repository.AICourseRepository;
import com.umc.yeogi_gal_lae.api.aiCourse.service.AICourseService;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai-course")
@RequiredArgsConstructor
public class AICourseController {

    private final TripPlanRepository tripPlanRepository;
    private final AICourseRepository aiCourseRepository;
    private final AICourseService aiCourseService;

    /**
     * POST /api/ai-course/gpt/{tripPlanId} TripPlan ID를 기반으로 GPT API를 호출하여 AICourse를 생성하고, 생성된 AICourse의 id를 반환합니다.
     */
    @PostMapping("/{tripPlanId}")
    public Response<AICourseResponse> generateAndStoreAICourse(@PathVariable Long tripPlanId) {
        Optional<TripPlan> tripPlanOpt = tripPlanRepository.findById(tripPlanId);
        if (tripPlanOpt.isEmpty()) {
            return Response.of(ErrorCode.NOT_FOUND, null);
        }
        TripPlan tripPlan = tripPlanOpt.get();
        // 생성된 AICourse 엔티티 반환
        var aiCourse = aiCourseService.generateAndStoreAICourse(tripPlan);
        if (aiCourse == null) {
            return Response.of(ErrorCode.INTERNAL_SERVER_ERROR, null);
        }
        return Response.of(SuccessCode.OK, AICourseConverter.toAICourseResponse(aiCourse));
    }

    /**
     * GET /api/ai-course/room/{roomId}/{courseId} Room ID와 courseId를 기반으로 저장된 AICourse를 조회하여 DTO로 반환합니다.
     */
    @GetMapping("/room/{roomId}/{aiCourseId}")
    public Response<List<DailyItineraryResponse>> getStoredAICourse(
            @PathVariable Long roomId,
            @PathVariable Long aiCourseId) {
        // AICourse 엔티티를 기본키(aiCourseId)로 조회
        Optional<AICourse> aiCourseOpt = aiCourseRepository.findById(aiCourseId);
        if (aiCourseOpt.isEmpty()) {
            return Response.of(ErrorCode.NOT_FOUND, null);
        }
        AICourse aiCourse = aiCourseOpt.get();
        // 해당 AICourse의 TripPlan에 연결된 Room의 id가 전달받은 roomId와 일치하는지 확인
        if (!aiCourse.getTripPlan().getRoom().getId().equals(roomId)) {
            return Response.of(ErrorCode.NOT_FOUND, null);
        }
        // 저장된 AICourse의 기본키를 사용해 일정 데이터를 도메인 모델로 변환
        Map<String, List<Place>> courseMap = aiCourseService.getStoredAICourseById(aiCourseId);
        if (courseMap.isEmpty()) {
            return Response.of(ErrorCode.NOT_FOUND, null);
        }
        // Converter를 이용하여 DailyItineraryResponse DTO 리스트로 변환
        List<DailyItineraryResponse> responseList = AICourseConverter.toDailyItineraryResponseList(courseMap);
        return Response.of(SuccessCode.OK, responseList);
    }
}
