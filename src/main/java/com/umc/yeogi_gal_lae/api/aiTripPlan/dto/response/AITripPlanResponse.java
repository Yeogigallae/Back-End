package com.umc.yeogi_gal_lae.api.AITripPlan.dto.response;


import com.umc.yeogi_gal_lae.api.place.dto.response.PlaceResponse;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class AITripPlanResponse {
    private Map<String, Day> data;

    @Getter
    @Builder
    public static class Day {
        private String date;
        private List<PlaceResponse> places;
    }
}
