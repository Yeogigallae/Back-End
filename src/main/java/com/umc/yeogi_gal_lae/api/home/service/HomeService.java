package com.umc.yeogi_gal_lae.api.home.service;

import com.umc.yeogi_gal_lae.api.home.converter.HomeConverter;
import com.umc.yeogi_gal_lae.api.home.dto.HomeResponse;
import com.umc.yeogi_gal_lae.api.home.repository.HomeRepository;
import com.umc.yeogi_gal_lae.api.room.repository.RoomMemberRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.types.Status;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeRepository homeRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final UserRepository userRepository;

    public Response<HomeResponse.OngoingVoteRoomList> getOngoingVoteRooms(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Long> userRoomIds = roomMemberRepository.findAllByUserId(user.getId())
                .stream()
                .map(roomMember -> roomMember.getRoom().getId())
                .collect(Collectors.toList());

        List<HomeResponse.OngoingVoteRoom> rooms = homeRepository.findAllOngoingVoteRooms().stream()
                .filter(voteRoom -> userRoomIds.contains(voteRoom.getTripPlan().getRoom().getId()))
                .map(HomeConverter::toOngoingVoteRoom)
                .collect(Collectors.toList());

        return Response.of(SuccessCode.ONGOING_VOTE_ROOMS_FETCH_OK, new HomeResponse.OngoingVoteRoomList(rooms.size(), rooms));
    }

    public Response<HomeResponse.CompletedVoteRoomList> getCompletedVoteRooms(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Long> userRoomIds = roomMemberRepository.findAllByUserId(user.getId())
                .stream()
                .map(roomMember -> roomMember.getRoom().getId())
                .collect(Collectors.toList());

        List<HomeResponse.CompletedVoteRoom> rooms = homeRepository.findByStatus(Status.COMPLETED).stream()
                .filter(tripPlan -> userRoomIds.contains(tripPlan.getRoom().getId()))
                .map(HomeConverter::toCompletedVoteRoom)
                .collect(Collectors.toList());

        return Response.of(SuccessCode.COMPLETED_VOTE_ROOMS_FETCH_OK, new HomeResponse.CompletedVoteRoomList(rooms.size(), rooms));
    }

    public Response<HomeResponse.CompletedTripPlanList> getCompletedTripPlans(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Long> userRoomIds = roomMemberRepository.findAllByUserId(user.getId())
                .stream()
                .map(roomMember -> roomMember.getRoom().getId())
                .collect(Collectors.toList());

        List<HomeResponse.CompletedTripPlan> trips = homeRepository.findByStatus(Status.COMPLETED).stream()
                .filter(tripPlan -> userRoomIds.contains(tripPlan.getRoom().getId()))
                .filter(tripPlan -> tripPlan.getEndDate().isBefore(LocalDate.now()))
                .map(HomeConverter::toCompletedTripPlan)
                .collect(Collectors.toList());
        return Response.of(SuccessCode.COMPLETED_TRIP_PLANS_FETCH_OK, new HomeResponse.CompletedTripPlanList(trips.size(), trips));
    }
}
