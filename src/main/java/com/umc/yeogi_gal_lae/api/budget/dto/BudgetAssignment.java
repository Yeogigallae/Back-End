package com.umc.yeogi_gal_lae.api.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetAssignment {
    private String placeName;
    private String budgetType;
    private Double recommendedAmount;
}