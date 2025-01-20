package com.umc.yeogi_gal_lae.api.tripPlan.dto;

import com.umc.yeogi_gal_lae.api.tripPlan.types.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TripPlanRequest {
    private String name; // 여행 이름
    private String location; // 여행 장소
    private String startDate;
    private String endDate;

    private TripPlanType tripPlanType; // "COURSE", "SCHEDULE", "BUDGET"
    private VoteLimitTime voteLimitTime; // "30분", "60분", "4시간", "6시간"
    private Integer minDays; // 최소 숙박일
    private Integer maxDays; // 최대 숙박일
    private String groupId; // 그룹 ID

    // 이너 클래스 정의
    @Getter
    @Setter
    public static class ScheduleDetails {
        private String message; // 일정 메시지
        private String price; // 가격 정보
    }

    @Getter
    @Setter
    public static class BudgetDetails {
        private Transportation transportation;
        private Accommodation accommodation;
        private Meal meal;
        private Integer people; // 인원 수
    }

    @Getter
    @Setter
    public static class CourseDetails {
        private String message; // 코스 메시지
    }

    private ScheduleDetails scheduleDetails;
    private BudgetDetails budgetDetails;
    private CourseDetails courseDetails;
}