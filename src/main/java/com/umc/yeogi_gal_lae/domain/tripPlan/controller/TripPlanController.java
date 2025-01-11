package com.umc.yeogi_gal_lae.domain.tripPlan.controller;

import com.umc.yeogi_gal_lae.domain.tripPlan.dto.TripPlanRequestDTO;
import com.umc.yeogi_gal_lae.domain.tripPlan.service.TripPlanService;
import com.umc.yeogi_gal_lae.domain.vote.dto.VoteRequest;
import com.umc.yeogi_gal_lae.domain.vote.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trip-plans")
@RequiredArgsConstructor
public class TripPlanController {

    private final TripPlanService tripPlanService;
//    private final VoteService voteService;
//    private final RoomService roomService;

    /**
     * 새로운 "코스짜기" 데이터를 특정 방에 공유합니다.
     *
     * @param roomId  방 ID
     * @param request 코스짜기 입력 데이터
     */
    @Operation(summary = "코스짜기 데이터 공유", description = "코스짜기 데이터를 특정 방에 공유합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "데이터 공유 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "방을 찾을 수 없음")
    })
    @PostMapping("/{roomId}/share-course")
    public ResponseEntity<Void> shareCourseData(@PathVariable Long roomId, @RequestBody TripPlanRequestDTO request) {
//        String message = String.format(
//                "투표 제한 시간: %s\n장소: %s\n기간: %s ~ %s",
//                request.getVoteTime(),
//                request.getLocation(),
//                request.getStartDate(),
//                request.getEndDate()
//        );
//
//        roomService.shareDataWithRoom(roomId, message);
        return ResponseEntity.ok().build();
    }

    /**
     * 새로운 "투표하기" 데이터를 특정 방에 공유합니다.
     *
     * @param roomId  방 ID
     * @param request 투표하기 입력 데이터
     */
    @Operation(summary = "투표하기 데이터 공유", description = "투표하기 데이터를 특정 방에 공유합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "데이터 공유 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "방을 찾을 수 없음")
    })
    @PostMapping("/{roomId}/share-vote")
    public ResponseEntity<Void> shareVoteData(@PathVariable Long roomId, @RequestBody VoteRequest request) {
//        String message = String.format(
//                "투표 제한 시간: %s\n장소: %s\n가격: %s\n기간: %s ~ %s",
//                request.getVoteTime(),
//                request.getLocation(),
//                request.getPrice(),
//                request.getStartDate(),
//                request.getEndDate()
//        );
//
//        roomService.shareDataWithRoom(roomId, message);
        return ResponseEntity.ok().build();
    }
}

