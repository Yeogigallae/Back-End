package com.umc.yeogi_gal_lae.api.home.controller;

import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeController {

    @Operation(summary = "알림 상태 조회", description = "사용자의 알림 확인 상태를 반환합니다.")
    @GetMapping("/home/{userId}/notifications-status")
    public Response<Boolean> getNotificationStatus(@PathVariable Long userId) {
        // 알림 상태 확인 로직 구현
        boolean status = true; // 예시로 true 반환
        return Response.of(SuccessCode.OK, status);
    }

    @Operation(summary = "진행 중인 투표 목록 조회", description = "사용자의 진행 중인 투표 목록을 반환합니다.")
    @GetMapping("/home/{userId}/votes")
    public Response<String> getVotes(@PathVariable Long userId) {
        // 투표 목록 로직 구현
        return Response.of(SuccessCode.OK, "진행 중인 투표 목록");
    }

    @Operation(summary = "예정된 여행 목록 조회", description = "사용자의 예정된 여행 목록을 반환합니다.")
    @GetMapping("/home/{userId}/planned-trips")
    public Response<String> getPlannedTrips(@PathVariable Long userId) {
        // 예정된 여행 목록 로직 구현
        return Response.of(SuccessCode.OK, "예정된 여행 목록");
    }

    @Operation(summary = "완료된 여행 조회", description = "사용자의 완료된 여행을 국내와 해외로 구분하여 반환합니다.")
    @GetMapping("/home/{userId}/completed-trips")
    public Response<String> getCompletedTrips(@PathVariable Long userId) {
        // 완료된 여행 목록 로직 구현
        return Response.of(SuccessCode.OK, "완료된 여행 목록");
    }
}
