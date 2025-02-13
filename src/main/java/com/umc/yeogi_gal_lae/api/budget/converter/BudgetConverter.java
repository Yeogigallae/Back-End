package com.umc.yeogi_gal_lae.api.budget.converter;

import com.umc.yeogi_gal_lae.api.budget.dto.BudgetResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BudgetConverter {

    /**
     * GPT API 결과 Map을 정렬된 BudgetResponse DTO 리스트로 변환합니다.
     *
     * @param budgetMap 키는 "1일차", "2일차" 등, 값은 해당 일차의 예산 추천 정보
     * @return 정렬된 BudgetResponse DTO 리스트
     */
    public static List<BudgetResponse> toBudgetResponseList(Map<String, BudgetResponse> budgetMap) {
        return budgetMap.entrySet().stream()
                .sorted((e1, e2) -> {
                    int day1 = extractDayNumber(e1.getKey());
                    int day2 = extractDayNumber(e2.getKey());
                    return Integer.compare(day1, day2);
                })
                .map(entry -> {
                    BudgetResponse response = entry.getValue();
                    // day 값이 없는 경우 Builder를 이용하여 day 값을 채워서 새 객체로 반환
                    if (response.getDay() == null || response.getDay().isEmpty()) {
                        return BudgetResponse.builder()
                                .day(entry.getKey())
                                .mealBudget(response.getMealBudget())
                                .activityBudget(response.getActivityBudget())
                                .shoppingBudget(response.getShoppingBudget())
                                .transportBudget(response.getTransportBudget())
                                .build();
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }

    private static int extractDayNumber(String dayLabel) {
        return Integer.parseInt(dayLabel.replaceAll("\\D", ""));
    }
}
