package com.umc.yeogi_gal_lae.domain.tripPlan.service;

import com.umc.yeogi_gal_lae.domain.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.domain.tripPlan.domain.TripType;
import com.umc.yeogi_gal_lae.domain.tripPlan.dto.TripPlanRequestDTO;
import com.umc.yeogi_gal_lae.domain.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.domain.user.domain.User;
import com.umc.yeogi_gal_lae.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TripPlanService {
    private final TripPlanRepository tripPlanRepository;
//    private final UserRepository userRepository;

    /**
     * 사용자의 완료된 여행 계획을 반환합니다.
     *
     * @param userId 사용자 ID
     * @return 완료된 여행 계획 목록
     */
    public List<TripPlan> getCompletedTrips(Long userId) {
        return tripPlanRepository.findCompletedTripsByUserId(userId);
    }

    /**
     * 사용자의 예정된 여행 계획을 반환합니다.
     *
     * @param userId 사용자 ID
     * @return 예정된 여행 계획 목록
     */
    public List<TripPlan> getUserPlannedTrips(Long userId) {
//         return tripPlanRepository.findPlannedTripsByUserId(userId);
    }

    // 여행 유형별 분류
    public Map<String, List<TripPlan>> groupTripsByType(List<TripPlan> trips) {
//        Map<String, List<TripPlan>> groupedTrips = new HashMap<>();
//        groupedTrips.put("DOMESTIC", new ArrayList<>());
//        groupedTrips.put("OVERSEAS", new ArrayList<>());
//
//        for (TripPlan trip : trips) {
//            if ("DOMESTIC".equals(trip.getTripType())) {
//                groupedTrips.get("DOMESTIC").add(trip);
//            } else if ("OVERSEAS".equals(trip.getTripType())) {
//                groupedTrips.get("OVERSEAS").add(trip);
//            }
//        }
//        return groupedTrips;
    }
}
