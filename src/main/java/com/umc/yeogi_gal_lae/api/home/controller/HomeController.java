//package com.umc.yeogi_gal_lae.api.home.controller;
//
//import com.umc.yeogi_gal_lae.api.home.dto.HomeDto;
//import com.umc.yeogi_gal_lae.api.home.service.HomeService;
//import com.umc.yeogi_gal_lae.global.common.response.Response;
//import com.umc.yeogi_gal_lae.global.success.SuccessCode;
//import io.swagger.v3.oas.annotations.Operation;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api")
//@RequiredArgsConstructor
//public class HomeController {
//
//    private final HomeService homeService;
//
//    @Operation(summary = "알림 상태 조회", description = "사용자의 알림 확인 상태를 반환합니다.")
//    @GetMapping("/home/{userId}/notifications-status")
//    public Response<Boolean> getNotificationStatus(@PathVariable Long userId) {
//        boolean status = homeService.getNotificationStatus(userId);
//        return Response.of(SuccessCode.OK, status);
//    }
//
//    @Operation(summary = "진행 중인 투표 목록 조회", description = "사용자의 진행 중인 투표 목록을 반환합니다.")
//    @GetMapping("/home/{userId}/votes")
//    public Response<List<HomeDto.VoteResponse>> getVotes(@PathVariable Long userId) {
//        List<HomeDto.VoteResponse> votes = homeService.getOngoingVotes(userId);
//        return Response.of(SuccessCode.OK, votes);
//    }
//
//    @Operation(summary = "예정된 여행 목록 조회", description = "사용자의 예정된 여행 목록을 반환합니다.")
//    @GetMapping("/home/{userId}/planned-trips")
//    public Response<List<HomeDto.PlannedTripResponse>> getPlannedTrips(@PathVariable Long userId) {
//        List<HomeDto.PlannedTripResponse> plannedTrips = homeService.getPlannedTrips(userId);
//        return Response.of(SuccessCode.OK, plannedTrips);
//    }
//
//    @Operation(summary = "완료된 여행 조회", description = "사용자의 완료된 여행을 국내와 해외로 구분하여 반환합니다.")
//    @GetMapping("/home/{userId}/completed-trips")
//    public Response<HomeDto.CompletedTripsResponse> getCompletedTrips(@PathVariable Long userId) {
//        HomeDto.CompletedTripsResponse completedTrips = homeService.getCompletedTrips(userId);
//        return Response.of(SuccessCode.OK, completedTrips);
//    }
//}
