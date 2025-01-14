package com.umc.yeogi_gal_lae.api.place.repository;

import com.umc.yeogi_gal_lae.api.place.domain.Place;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findAllByTripPlanId(Long TripPlanId);
}
