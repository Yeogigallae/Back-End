package com.umc.yeogi_gal_lae.api.itinerary.converter;

import com.umc.yeogi_gal_lae.api.itinerary.dto.DailyItineraryResponse;
import com.umc.yeogi_gal_lae.api.place.converter.PlaceConverter;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.place.dto.response.PlaceResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ItineraryConverter {

    public static PlaceResponse toPlaceResponse(Place place) {
        if (place == null) {
            return null;
        }
        return PlaceResponse.builder()
                .placeName(place.getPlaceName())
                .address(place.getAddress())
                .lat(place.getLatitude())
                .lng(place.getLongitude())
                // 필요한 다른 필드들을 매핑
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
}
