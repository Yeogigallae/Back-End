package com.umc.yeogi_gal_lae.api.home.controller;

import com.umc.yeogi_gal_lae.api.home.mapper.HomeResponseMapper;
import com.umc.yeogi_gal_lae.api.home.service.HomeService;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.service.TripPlanService;
import com.umc.yeogi_gal_lae.api.vote.dto.VoteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;
    private final TripPlanService tripPlanService;
    private final HomeResponseMapper homeResponseMapper;
//    private final NotificationService notificationService;

//    /**
//     * 모든 알림 확인 여부 조회 API.
//     * 사용자가 모든 알림을 확인했는지 여부를 반환합니다.
//     *
//     * @param userId 사용자 ID
//     * @return 알림 확인 여부 (boolean)
//     */
//    @Operation(summary = "알림 확인 여부 조회",
//            description = "사용자가 모든 알림을 확인했는지 여부를 반환합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "성공적으로 알림 확인 여부를 반환함"),
//            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
//    })
//    @GetMapping("/{userId}/notifications-status")
//    public ResponseEntity<Map<String, Boolean>> getNotificationStatus(@PathVariable Long userId) {
//        boolean allRead = notificationService.checkAllNotificationsRead(userId);
//
//        Map<String, Boolean> response = new HashMap<>();
//        response.put("allRead", allRead);
//
//        return ResponseEntity.ok(response);
//    }

//    /**
//     * 투표 중인 방 목록 조회 API.
//     * 남은 투표 시간, 방 이름, 장소, 투표 완료 인원을 반환합니다.
//     *
//     * @param userId 사용자 ID
//     * @return 투표 중인 방 정보
//     */
//    @Operation(summary = "투표 중인 방 목록 조회",
//            description = "사용자가 참여 중인 투표 방의 남은 시간과 수, 방 이름, 장소, 투표 완료 인원을 반환합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "성공적으로 투표 중인 방 목록을 반환함"),
//            @ApiResponse(responseCode = "404", description = "투표 중인 방이 없음"),
//            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
//    })
//    @GetMapping("/{userId}/votes")
//    public ResponseEntity<Map<String, Object>> getActiveVoteRooms(@PathVariable Long userId) {
//        // 중복 및 안쓰이는 변수
//        // List<Map<String, Object>> rooms = homeService.getActiveVoteRooms(userId);
//        // Map<String, Object> response = homeResponseMapper.mapActiveVoteRoomsToResponse(rooms);
//
//        // List<VoteResponse.VoteDTO> votes = homeService.getUserVotes(userId);
//
////        List<Map<String, Object>> rooms = votes.stream().map(vote -> {
////            Map<String, Object> voteInfo = new HashMap<>();
////            voteInfo.put("roomName", vote.getName()); // 방 이름
////            voteInfo.put("location", "투표 장소"); // 장소 (데이터가 있을 경우 교체 필요)
//////            voteInfo.put("remainingTime", calculateRemainingTime(vote.getId())); // 남은 투표 시간 계산
////            voteInfo.put("completedParticipants", vote.getCount()); // 투표 완료 인원
////            return voteInfo;
////        }).collect(Collectors.toList());
////
////        Map<String, Object> response = new HashMap<>();
////        response.put("count", votes.size()); // 투표 중인 방 개수
////        response.put("rooms", rooms);
//
////        return ResponseEntity.ok(response);
////    }

    /**
     * 예정된 여행 목록 조회 API.
     * 방 이름, 장소, 투표 시작 및 종료 시간을 반환합니다.
     *
     * @param userId 사용자 ID
     * @return 예정된 여행 정보
     */
    @Operation(summary = "예정된 여행 목록 조회",
            description = "사용자의 예정된 여행 계획의 방 이름과 수, 장소, 투표 시작 및 종료 시간을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 예정된 여행 목록을 반환함"),
            @ApiResponse(responseCode = "404", description = "예정된 여행이 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/{userId}/planned-trips")
    public ResponseEntity<Map<String, Object>> getPlannedTrips(@PathVariable Long userId) {
        List<TripPlan> plannedTrips = tripPlanService.getUserPlannedTrips(userId);

        // 사용 되지 않는 코드라 일단 주석 처리
        // Map<String, Object> response = homeResponseMapper.mapPlannedTripsToResponse(plannedTrips);
        List<Map<String, Object>> trips = plannedTrips.stream().map(trip -> {
            Map<String, Object> tripInfo = new HashMap<>();
            tripInfo.put("roomName", trip.getName()); // 방 이름
            tripInfo.put("location", trip.getLocation()); // 장소
            tripInfo.put("startTime", trip.getStartDate()); // 투표 시작 시간
            tripInfo.put("endTime", trip.getEndDate()); // 투표 종료 시간
            return tripInfo;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("count", plannedTrips.size()); // 예정된 방 개수
        response.put("trips", trips);

        return ResponseEntity.ok(response);
    }

    /**
     * 완료된 여행 목록 조회 API.
     * 국내/해외로 분리하여 완료된 여행을 반환합니다.
     *
     * @param userId 사용자 ID
     * @return 완료된 여행 정보
     */
    @Operation(summary = "완료된 여행 목록 조회",
            description = "사용자의 완료된 여행을 국내와 해외로 분리하여 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 완료된 여행 목록을 반환함"),
            @ApiResponse(responseCode = "404", description = "완료된 여행이 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/{userId}/completed-trips")
    public ResponseEntity<Map<String, Object>> getCompletedTrips(@PathVariable Long userId) {
        List<TripPlan> completedTrips = tripPlanService.getCompletedTrips(userId);

        Map<String, List<TripPlan>> tripsByType = tripPlanService.groupTripsByType(completedTrips);

        Map<String, Object> response = new HashMap<>();
        response.put("domesticCount", tripsByType.get("DOMESTIC").size()); // 국내 여행 개수
        response.put("overseasCount", tripsByType.get("OVERSEAS").size()); // 해외 여행 개수
        response.put("domesticTrips", tripsByType.get("DOMESTIC"));
        response.put("overseasTrips", tripsByType.get("OVERSEAS"));

        return ResponseEntity.ok(response);
    }
}