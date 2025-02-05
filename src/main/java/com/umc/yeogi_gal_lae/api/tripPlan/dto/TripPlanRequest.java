package com.umc.yeogi_gal_lae.api.tripPlan.dto;

import com.umc.yeogi_gal_lae.api.tripPlan.types.*;
import lombok.Getter;
import lombok.Setter;

@Getter
public class TripPlanRequest {
    private String name; // 여행 이름
    private String location; // 여행 장소
    private String startDate;
    private String endDate;

    private TripType tripType;
    private VoteLimitTime voteLimitTime; // "30분", "60분", "4시간", "6시간"
    private Integer minDays; // 최소 숙박일
    private Integer maxDays; // 최대 숙박일
    private Long userId; // 유저 ID
    private Long roomId; // 방 ID 추가
    private String imageUrl;

    // 이너 클래스 정의
    @Getter
    public static class ScheduleDetails {
        private String message; // 일정 메시지
        private String price; // 가격 정보
    }

    @Getter
    public static class CourseDetails {
        private String message; // 코스 메시지
    }

    private ScheduleDetails scheduleDetails;
    private CourseDetails courseDetails;
}