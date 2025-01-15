package com.umc.yeogi_gal_lae.api.place.service;

import com.umc.yeogi_gal_lae.api.place.converter.PlaceConverter;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.place.dto.request.PlaceRequest;
import com.umc.yeogi_gal_lae.api.place.repository.PlaceRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final TripPlanRepository tripPlanRepository;

    @Transactional
    public Place addPlace(PlaceRequest placeRequest) {
        Place place = PlaceConverter.toPlaceEntity(placeRequest);
        placeRepository.save(place);

        return place;
    }

    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    public Place getPlaceById(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));
    }

    @Transactional
    public void deletePlace(Long placeId) {
        placeRepository.deleteById(placeId);
    }
}
