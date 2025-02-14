package com.umc.yeogi_gal_lae.api.place.converter;

import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.place.dto.request.PlaceRequest;
import com.umc.yeogi_gal_lae.api.place.dto.response.AllPlaceResponse;
import com.umc.yeogi_gal_lae.api.place.dto.response.PlaceResponse;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;

public class PlaceConverter {

    public static Place toPlaceEntity(Room room, PlaceRequest request, User user) {
        return Place.builder()
                .room(room)
                .placeName(request.getPlaceName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .user(user)
                .build();
    }

    public static PlaceResponse toPlaceResponse(Place place) {
        return new PlaceResponse(
                place.getId(),
                place.getPlaceName(),
                place.getAddress(),
                place.getLatitude(),
                place.getLongitude(),
                place.getImageUrl(),
                place.getDescription(),
                place.getUser().getUsername(),
                place.getUser().getProfileImage()
        );
    }

    public static AllPlaceResponse toAllPlaceResponse(List<Place> places) {
        List<PlaceResponse> placeResponses = places.stream()
                .map(PlaceConverter::toPlaceResponse)
                .collect(Collectors.toList());
        return new AllPlaceResponse(placeResponses);
    }
}
