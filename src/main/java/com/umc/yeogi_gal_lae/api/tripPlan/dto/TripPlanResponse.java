package com.umc.yeogi_gal_lae.api.tripPlan.dto;

import com.umc.yeogi_gal_lae.api.tripPlan.types.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TripPlanResponse {
    private Long id; // 여행 계획 ID
    private String name; // 여행 이름
    private String location; // 여행 장소
    private String description;
    private String startDate;
    private String endDate;
    private String price; // 가격 정보
    private String imageUrl;
    private TripPlanType tripPlanType; // "COURSE", "SCHEDULE", "BUDGET"
    private TripType tripType;
    private VoteLimitTime voteLimitTime; // "30분", "60분", "4시간", "6시간"
    private Integer minDays; // 최소 숙박일
    private Integer maxDays; // 최대 숙박일
    private String groupName;
}