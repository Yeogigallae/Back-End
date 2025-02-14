package com.umc.yeogi_gal_lae.api.budget.domain;

import com.umc.yeogi_gal_lae.api.aiCourse.domain.AICourse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // GPT API로 산출한 예산 추천 결과를 JSON 형식으로 저장
    @Column(name = "budget_json", columnDefinition = "TEXT", nullable = false)
    private String budgetJson;

    // AICourse와 1:1 관계 (한 AICourse에 대해 하나의 예산 추천 기록)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_course_id", nullable = false, unique = true)
    private AICourse aiCourse;
}

