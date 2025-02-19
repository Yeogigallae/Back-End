package com.umc.yeogi_gal_lae.api.budget.controller;

import com.umc.yeogi_gal_lae.api.budget.converter.BudgetConverter;
import com.umc.yeogi_gal_lae.api.budget.domain.Budget;
import com.umc.yeogi_gal_lae.api.budget.dto.AICourseBudgetResponse;
import com.umc.yeogi_gal_lae.api.budget.dto.BudgetAssignment;
import com.umc.yeogi_gal_lae.api.budget.dto.BudgetDetailResponse;
import com.umc.yeogi_gal_lae.api.budget.dto.BudgetResponse;
import com.umc.yeogi_gal_lae.api.budget.dto.DailyBudgetAssignmentResponse;
import com.umc.yeogi_gal_lae.api.budget.repository.BudgetRepository;
import com.umc.yeogi_gal_lae.api.budget.service.BudgetService;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final BudgetRepository budgetRepository;

    /**
     * POST /api/budget/gpt/{aiCourseId} aiCourseId를 기반으로 예산 추천을 생성 및 저장하고, 생성된 Budget의 정보를 반환합니다.
     */
    @PostMapping("/aiCourse/{aiCourseId}")
    public Response<BudgetResponse> generateAndStoreBudget(@PathVariable Long aiCourseId) {
        var budget = budgetService.generateAndStoreBudget(aiCourseId);
        if (budget == null) {
            return Response.of(ErrorCode.INTERNAL_SERVER_ERROR, null);
        }
        return Response.of(SuccessCode.OK, BudgetConverter.toBudgetResponse(budget));
    }

    /**
     * GET /api/budget/{id} Budget 엔티티의 id로 저장된 예산 추천 데이터를 조회하고, 이를 DailyBudgetAssignmentResponse DTO 리스트로 변환하여 반환합니다.
     */
    @GetMapping("/{budgetId}")
    public Response<BudgetDetailResponse> getBudget(@PathVariable Long budgetId) {
        Optional<Budget> budgetOpt = budgetService.getBudgetById(budgetId);
        if (!budgetOpt.isPresent()) {
            return Response.of(ErrorCode.NOT_FOUND, null);
        }
        Budget budget = budgetOpt.get();
        Map<String, List<BudgetAssignment>> budgetMap = budgetService.getBudgetMapById(budgetId);
        List<DailyBudgetAssignmentResponse> dailyAssignments = BudgetConverter.toDailyBudgetAssignmentResponseList(
                budgetMap);

        // AICourse를 통해 TripPlan 정보를 조회 (TripPlan 클래스가 startDate와 endDate를 LocalDate 타입으로 제공한다고 가정)
        String imageUrl = budget.getAiCourse().getTripPlan().getImageUrl();
        LocalDate startDate = budget.getAiCourse().getTripPlan().getStartDate();
        LocalDate endDate = budget.getAiCourse().getTripPlan().getEndDate();

        BudgetDetailResponse detailResponse = BudgetDetailResponse.builder()
                .dailyAssignments(dailyAssignments)
                .imageUrl(imageUrl)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return Response.of(SuccessCode.OK, detailResponse);
    }


    @GetMapping("/{aiCourseId}/budgetIds")
    public Response<List<AICourseBudgetResponse>> getBudgetIdsByAiCourseId(@PathVariable Long aiCourseId) {
        List<com.umc.yeogi_gal_lae.api.budget.domain.Budget> budgets =
                budgetRepository.findByAiCourseId(aiCourseId);
        if (budgets.isEmpty()) {
            return Response.of(ErrorCode.NOT_FOUND, Collections.emptyList());
        }
        List<AICourseBudgetResponse> responseList = budgets.stream()
                .map(budget -> new AICourseBudgetResponse(budget.getId()))
                .collect(Collectors.toList());
        return Response.of(SuccessCode.OK, responseList);
    }

}
