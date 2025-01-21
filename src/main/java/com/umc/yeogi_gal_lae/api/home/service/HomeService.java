//package com.umc.yeogi_gal_lae.api.home.service;
//
//import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
//import com.umc.yeogi_gal_lae.api.vote.repository.VoteRepository;
//import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class HomeService {
//
//    private final UserRepository userRepository;
//    private final VoteRepository voteRepository;
//    private final TripPlanRepository tripPlanRepository;
//
//    public boolean getNotificationStatus(Long userId) {
//        return userRepository.existsById(userId);
//    }
//
//    public List<HomeDto.VoteResponse> getOngoingVotes(Long userId) {
//        return HomeConverter.toVoteResponses(voteRepository.findOngoingVotesByUserId(userId));
//    }
//
//    public List<HomeDto.PlannedTripResponse> getPlannedTrips(Long userId) {
//        return HomeConverter.toPlannedTripResponses(tripPlanRepository.findPlannedTripsByUserId(userId));
//    }
//
//    public HomeDto.CompletedTripsResponse getCompletedTrips(Long userId) {
//        return HomeConverter.toCompletedTripsResponse(
//                tripPlanRepository.findCompletedTripsByUserId(userId));
//    }
//}
