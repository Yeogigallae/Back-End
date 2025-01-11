package com.umc.yeogi_gal_lae.domain.tripPlan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripPlanRequestDTO {
    private String name;          // 여행 계획 이름
    private String description;   // 여행 계획 설명
    private String tripType;      // 여행 유형 (예: DOMESTIC, OVERSEAS)
    private String location;      // 여행 장소
    private LocalDate startDate;  // 시작일
    private LocalDate endDate;    // 종료일
    private Long userId;          // 사용자 ID
}
