package com.umc.yeogi_gal_lae.api.place.controller;

import com.umc.yeogi_gal_lae.api.place.converter.PlaceConverter;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.place.dto.request.PlaceRequest;
import com.umc.yeogi_gal_lae.api.place.dto.response.AllPlaceResponse;
import com.umc.yeogi_gal_lae.api.place.dto.response.PlaceResponse;
import com.umc.yeogi_gal_lae.api.place.service.PlaceService;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class PlaceController {

    private final PlaceService placeService;

    @Operation(summary = "여행 장소 추가")
    @PostMapping("/{roomId}/places")
    public Response<PlaceResponse> addPlace(@PathVariable Long roomId, @RequestBody PlaceRequest request) {
        Place place = placeService.addPlace(roomId, request);
        PlaceResponse response = PlaceConverter.toPlaceResponse(place);
        return Response.of(SuccessCode.OK, response);
    }

    @Operation(summary = "특정 장소 가져오기")
    @GetMapping("/roomId/places/{placeId}")
    public Response<Place> getPlaceById(
            @PathVariable Long roomId,
            @PathVariable Long placeId) {
        Place place = placeService.getPlaceById(roomId, placeId);
        return Response.of(SuccessCode.PLACES_FETCHED, place);
    }

    @Operation(summary = "특정 장소 삭제")
    @DeleteMapping("/{roomId}/places/{placeId}")
    public Response<Void> deletePlace(
            @PathVariable Long roomId,
            @PathVariable Long placeId) {
        placeService.deletePlaceById(roomId, placeId);
        return Response.ok(SuccessCode.PLACE_DELETED);
    }

    @Operation(summary = "모든 장소 가져오기")
    @GetMapping({"/{roomId}/places"})
    public Response<AllPlaceResponse> getAllPlaces(@PathVariable Long roomId) {
        List<Place> places = placeService.getAllPlacesByRoomId(roomId);
        AllPlaceResponse response = PlaceConverter.toAllPlaceResponse(places);
        return Response.of(SuccessCode.OK, response);

    }

    // 추가적인 예외 처리기를 여기에 추가할 수 있습니다.
}
