//package com.umc.yeogi_gal_lae.api.budget.controller;
//
//import com.umc.yeogi_gal_lae.api.budget.converter.BudgetConverter;
//import com.umc.yeogi_gal_lae.api.budget.dto.BudgetResponse;
//import com.umc.yeogi_gal_lae.api.budget.service.BudgetService;
//import com.umc.yeogi_gal_lae.api.itinerary.service.ItineraryService;
//import com.umc.yeogi_gal_lae.api.place.domain.Place;
//import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
//import com.umc.yeogi_gal_lae.global.common.response.Response;
//import com.umc.yeogi_gal_lae.global.error.ErrorCode;
//import com.umc.yeogi_gal_lae.global.success.SuccessCode;
//import java.util.List;
//import java.util.Map;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/budget")
//@RequiredArgsConstructor
//public class BudgetController {
//
//    private final TripPlanRepository tripPlanRepository;
//    private final ItineraryService itineraryService;
//    private final BudgetService budgetService;
//
//    /**
//     * GET /api/budget/gpt/{tripPlanId} TripPlan의 일정 정보를 기반으로 각 일차별 식사비, 활동비, 쇼핑비, 교통비를 추천합니다.
//     *
//     * @param tripPlanId 여행 계획 ID
//     * @return BudgetResponse DTO 리스트를 포함한 응답
//     */
//    @GetMapping("/gpt/{tripPlanId}")
//    public Response<List<BudgetResponse>> generateBudgetRecommendations(@PathVariable Long tripPlanId) {
//        return tripPlanRepository.findById(tripPlanId)
//                .map(tripPlan -> {
//                    Map<String, List<Place>> itineraryMap = itineraryService.generateItinerary(tripPlan);
//                    Map<String, BudgetResponse> budgetMap = budgetService.generateBudgetRecommendations(itineraryMap);
//                    List<BudgetResponse> responseList = BudgetConverter.toBudgetResponseList(budgetMap);
//                    return Response.of(SuccessCode.OK, responseList);
//                })
//                .orElse(Response.of(ErrorCode.NOT_FOUND, null));
//    }
//
//}
