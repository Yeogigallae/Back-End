package com.umc.yeogi_gal_lae.api.place.controller;

import com.umc.yeogi_gal_lae.api.place.converter.PlaceConverter;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.place.dto.request.PlaceRequest;
import com.umc.yeogi_gal_lae.api.place.dto.response.AllPlaceResponse;
import com.umc.yeogi_gal_lae.api.place.dto.response.PlaceResponse;
import com.umc.yeogi_gal_lae.api.place.service.PlaceService;
import com.umc.yeogi_gal_lae.global.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trip-places")
public class PlaceController {

    private final PlaceService placeService;

    @Operation(summary = "여행 장소 추가")
    @PostMapping
    public Response<PlaceResponse> addPlace(@Valid @RequestBody PlaceRequest request) {
        Place place = placeService.addPlace(request);
        PlaceResponse response = PlaceConverter.toPlaceResponse(place);
        return Response.of(Code, response);
    }

    @GetMapping("/{id}")
    public Response<PlaceResponse> getPlaceById(@PathVariable Long id) {
        PlaceResponse response = placeService.getPlaceById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public Response<AllPlaceResponse> getAllPlaces() {

        List<Place> places = placeService
        List<PlaceResponse> responses = placeService.getAllPlaces();
        return ResponseEntity.ok(responses);
    }

    // 추가적인 예외 처리기를 여기에 추가할 수 있습니다.
}
