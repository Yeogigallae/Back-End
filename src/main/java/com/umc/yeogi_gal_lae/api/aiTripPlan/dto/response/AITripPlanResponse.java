package com.umc.yeogi_gal_lae.api.aiTripPlan.dto.response;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor              // ★ Jackson 역직렬화를 위한 기본 생성자
@AllArgsConstructor             // Builder 생성자와 함께 사용할 수 있음
@Builder
public class AITripPlanResponse {

    private Map<String, Day> data;

    @JsonAnySetter
    public void setAnyDay(String dayKey, Day dayValue) {
        if (data == null) {
            data = new HashMap<>();
        }
        data.put(dayKey, dayValue);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Day {
        private List<PlaceResponse> places;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceResponse {
        private Long placeId;
        private String placeName;
        private String address;
        private Double lat;
        private Double lng;
    }
}
