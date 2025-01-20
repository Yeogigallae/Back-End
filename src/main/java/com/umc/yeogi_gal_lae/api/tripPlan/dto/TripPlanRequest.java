package com.umc.yeogi_gal_lae.api.tripPlan.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TripPlanRequest {
    private String name; // 여행 이름
    private String location; // 여행 장소
    private String description;
    private String startDate;
    private String endDate;

    // 투표 생성 관련 필드
    private String voteType; // "COURSE", "SCHEDULE", "BUDGET"
    private String voteTitle;
    private String voteMessage;
    private String voteLimitTime; // "30분/60분/4시간/6시간"
    private Integer minDays; // 최소 숙박일
    private Integer maxDays; // 최대 숙박일
    private String groupId; // 그룹 ID
}
