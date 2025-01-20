package com.umc.yeogi_gal_lae.api.aiTripPlan.dto.response;


import com.umc.yeogi_gal_lae.api.place.domain.Place;
import java.util.List;
import java.util.Map;

public class AITripPlanResponse {
    private Map<String, Day> data;

    public static class Day {
        private List<Place> places;
        private String date;
    }
}
