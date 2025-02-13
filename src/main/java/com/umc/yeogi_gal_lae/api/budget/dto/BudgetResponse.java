package com.umc.yeogi_gal_lae.api.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BudgetResponse {
    private String day;
    private double mealBudget;
    private double activityBudget;
    private double shoppingBudget;
    private double transportBudget;
}
