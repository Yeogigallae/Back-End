package com.umc.yeogi_gal_lae.api.itinerary.controller;

import com.umc.yeogi_gal_lae.api.itinerary.converter.ItineraryConverter;
import com.umc.yeogi_gal_lae.api.itinerary.dto.DailyItineraryResponse;
import com.umc.yeogi_gal_lae.api.itinerary.service.ItineraryService;
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


    @PostMapping("/gpt/{tripPlanId}")
    public Response<List<DailyItineraryResponse>> generateAndStoreItinerary(@PathVariable Long tripPlanId) {
        Optional<TripPlan> tripPlanOpt = tripPlanRepository.findById(tripPlanId);
        if (tripPlanOpt.isEmpty()) {
            return Response.of(ErrorCode.NOT_FOUND, null);
        }
        TripPlan tripPlan = tripPlanOpt.get();
        Map<String, List<Place>> itineraryMap = itineraryService.generateAndStoreItinerary(tripPlan);
        List<DailyItineraryResponse> responseList = ItineraryConverter.toDailyItineraryResponseList(itineraryMap);
        return Response.of(SuccessCode.OK, responseList);
    }

    /**
     * GET /api/itinerary/room/{roomId} Room ID로 저장된 TripPlan의 Itinerary(일정)을 조회하여 반환합니다.
     *
     * @param roomId 방 ID
     * @return DailyItineraryResponse DTO 리스트를 포함한 응답
     */
    @GetMapping("/room/{roomId}")
    public Response<List<DailyItineraryResponse>> getStoredItineraryByRoom(@PathVariable Long roomId) {
        // 여러 TripPlan이 있을 수 있으므로, 예시에서는 첫 번째 TripPlan을 사용합니다.
        List<TripPlan> tripPlans = tripPlanRepository.findByRoomId(roomId);
        if (tripPlans.isEmpty()) {
            return Response.of(ErrorCode.NOT_FOUND, null);
        }
        TripPlan tripPlan = tripPlans.get(0);
        Map<String, List<Place>> itineraryMap = itineraryService.getStoredItinerary(tripPlan);
        if (itineraryMap.isEmpty()) {
            return Response.of(ErrorCode.NOT_FOUND, null);
        }
        List<DailyItineraryResponse> responseList = ItineraryConverter.toDailyItineraryResponseList(itineraryMap);
        return Response.of(SuccessCode.OK, responseList);
    }

}
