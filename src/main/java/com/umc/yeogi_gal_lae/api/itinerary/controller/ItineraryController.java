package com.umc.yeogi_gal_lae.api.itinerary.controller;

import com.umc.yeogi_gal_lae.api.itinerary.converter.ItineraryConverter;
import com.umc.yeogi_gal_lae.api.itinerary.dto.DailyItineraryResponse;
import com.umc.yeogi_gal_lae.api.itinerary.service.ItineraryService;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/itinerary")
@RequiredArgsConstructor
public class ItineraryController {

    private final ItineraryService itineraryService;
    private final TripPlanRepository tripPlanRepository;

    /**
     * GET /api/itinerary/gpt/{tripPlanId} TripPlan의 startDate, endDate에 따라 총 여행 일수에 맞게 "1일차", "2일차", ... 등의 일정과, 각 일차에
     * 추천할 장소 목록을 반환합니다.
     *
     * @param tripPlanId 여행 계획 ID
     * @return DailyItineraryResponse DTO 리스트를 포함한 응답
     */
    @GetMapping("/gpt/{tripPlanId}")
    public Response<List<DailyItineraryResponse>> generateItinerary(@PathVariable Long tripPlanId) {
        return tripPlanRepository.findById(tripPlanId)
                .map(tripPlan -> {
                    Map<String, List<Place>> itineraryMap = itineraryService.generateItinerary(tripPlan);
                    List<DailyItineraryResponse> responseList = ItineraryConverter.toDailyItineraryResponse(
                            itineraryMap);
                    return Response.of(SuccessCode.OK, responseList);
                })
                .orElse(Response.of(ErrorCode.NOT_FOUND, null));
    }
}
