package com.umc.yeogi_gal_lae.api.place.service;

import com.umc.yeogi_gal_lae.api.place.converter.PlaceConverter;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.place.dto.request.PlaceRequest;
import com.umc.yeogi_gal_lae.api.place.repository.PlaceRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
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
    public Place addPlace(PlaceRequest PlaceRequest) {
        Place place = PlaceConverter.toPlaceEntity(PlaceRequest);
        placeRepository.save(place);

        return place;
    }

//    public Place getPlace(Long placeId) {
//        return placeRepository.findById(placeId);
//    }

    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }
}
