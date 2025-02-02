package com.umc.yeogi_gal_lae.api.home.controller;

import com.umc.yeogi_gal_lae.api.home.dto.HomeResponse;
import com.umc.yeogi_gal_lae.api.home.service.HomeService;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @Operation(summary = "진행 중인 투표방 조회", description = "사용자가 속한 진행 중인 투표방과 개수를 조회합니다.")
    @GetMapping("/ongoing-vote-rooms")
    public Response<HomeResponse.OngoingVoteRoomList> getOngoingVoteRooms(@RequestParam String userEmail) {
        return homeService.getOngoingVoteRooms(userEmail);
    }

    @Operation(summary = "완료된 투표방 조회", description = "사용자가 속한 완료된 투표방과 개수를 조회합니다.")
    @GetMapping("/completed-vote-rooms")
    public Response<HomeResponse.CompletedVoteRoomList> getCompletedVoteRooms(@RequestParam String userEmail) {
        return homeService.getCompletedVoteRooms(userEmail);
    }

    @Operation(summary = "완료된 여행 계획 조회", description = "사용자가 속한 완료된 여행 계획과 개수를 조회합니다.")
    @GetMapping("/completed-trip-plans")
    public Response<HomeResponse.CompletedTripPlanList> getCompletedTripPlans(@RequestParam String userEmail) {
        return homeService.getCompletedTripPlans(userEmail);
    }
}
