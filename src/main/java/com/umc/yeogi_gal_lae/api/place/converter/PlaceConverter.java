package com.umc.yeogi_gal_lae.api.place.converter;

import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.place.dto.request.PlaceRequest;
import com.umc.yeogi_gal_lae.api.place.dto.response.AllPlaceResponse;
import com.umc.yeogi_gal_lae.api.place.dto.response.PlaceResponse;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import java.util.List;
import java.util.stream.Collectors;

public class PlaceConverter {
    // 요청 DTO -> 엔티티 변환
    public static Place toPlaceEntity(Room room, PlaceRequest tripPlaceRequest) {
        return Place.builder()
                .room(room)
                .placeName(tripPlaceRequest.getPlaceName())
                .address(tripPlaceRequest.getAddress())
                .latitude(tripPlaceRequest.getLatitude())
                .longitude(tripPlaceRequest.getLongitude())
                .build();
    }

    public static PlaceResponse toPlaceResponse(Place place) {
        return PlaceResponse.builder()
                .roomId(place.getRoom().getId())
                .placeId(place.getId())
                .placeName(place.getPlaceName())
                .address(place.getAddress())
                .lat(place.getLatitude())
                .lng(place.getLongitude())
                .build();
    }

    public static AllPlaceResponse toAllPlaceResponse(List<Place> places) {
        return AllPlaceResponse.builder()
                .placeResponses(toAllPlaceResponses(places))
                .build();
    }

    public static List<PlaceResponse> toAllPlaceResponses(List<Place> places) {
        return places.stream()
                .map(PlaceConverter::toPlaceResponse)
                .collect(Collectors.toList());
    }
}
