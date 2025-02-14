package com.umc.yeogi_gal_lae.api.aiCourse.converter;

import com.umc.yeogi_gal_lae.api.aiCourse.domain.AICourse;
import com.umc.yeogi_gal_lae.api.aiCourse.dto.AICourseResponse;
import com.umc.yeogi_gal_lae.api.aiCourse.dto.DailyItineraryResponse;
import com.umc.yeogi_gal_lae.api.place.converter.PlaceConverter;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.place.dto.response.PlaceResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class AICourseConverter {

    public static PlaceResponse toPlaceResponse(Place place) {
        if (place == null) {
            return null;
        }
        return PlaceResponse.builder()
                .placeName(place.getPlaceName())
                .address(place.getAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .build();
    }

    public static List<DailyItineraryResponse> toDailyItineraryResponse(Map<String, List<Place>> itineraryMap) {
        return itineraryMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey()) // "1일차", "2일차" 순으로 정렬
                .map(entry -> DailyItineraryResponse.builder()
                        .day(entry.getKey())
                        .places(entry.getValue().stream()
                                .map(PlaceConverter::toPlaceResponse)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    public static List<DailyItineraryResponse> toDailyItineraryResponseList(Map<String, List<Place>> itineraryMap) {
        return itineraryMap.entrySet().stream()
                .sorted((e1, e2) -> {
                    int day1 = Integer.parseInt(e1.getKey().replaceAll("\\D", ""));
                    int day2 = Integer.parseInt(e2.getKey().replaceAll("\\D", ""));
                    return Integer.compare(day1, day2);
                })
                .map(entry -> DailyItineraryResponse.builder()
                        .day(entry.getKey())
                        .places(entry.getValue().stream()
                                .map(PlaceConverter::toPlaceResponse)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    public static AICourseResponse toAICourseResponse(AICourse aiCourse) {
        return AICourseResponse.builder()
                .id(aiCourse.getId())
                .tripPlanId(aiCourse.getTripPlan().getId())
                .roomId(aiCourse.getTripPlan().getRoom().getId())
                .build();
    }
}
