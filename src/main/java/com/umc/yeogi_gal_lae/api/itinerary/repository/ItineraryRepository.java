package com.umc.yeogi_gal_lae.api.itinerary.repository;

import com.umc.yeogi_gal_lae.api.itinerary.domain.Itinerary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    Optional<Itinerary> findByTripPlanId(Long tripPlanId);
}
