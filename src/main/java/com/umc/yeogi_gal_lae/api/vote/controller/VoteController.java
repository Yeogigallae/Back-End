package com.umc.yeogi_gal_lae.api.vote.controller;

import com.umc.yeogi_gal_lae.api.vote.AuthenticatedUserUtils;
import com.umc.yeogi_gal_lae.api.vote.dto.request.VoteRequest;
import com.umc.yeogi_gal_lae.api.vote.dto.VoteResponse;
import com.umc.yeogi_gal_lae.api.vote.dto.request.VoteRoomRequest;
import com.umc.yeogi_gal_lae.api.vote.service.ValidVoteResultService;
import com.umc.yeogi_gal_lae.api.vote.service.VoteService;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class VoteController {

    @Autowired
    private final VoteService voteService;
    private final ValidVoteResultService validVoteResultService;

    @Validated
    @Operation(summary = "투표방 생성 API", description = "등록된 여행 계획에 대한 투표방을 생성합니다.")
    @PostMapping("/vote/new-room")
    public Response<Void> createVoteRoom(@RequestBody @Valid VoteRequest.createVoteRoomReq voteRequest) {

        voteService.createVoteRoom(voteRequest);

        return Response.of(SuccessCode.VOTE_ROOM_CREATED_OK);
    }


    @Validated
    @Operation(summary = "투표 API", description = "현재 사용자의 투표 요청 입니다.")
    @PostMapping("/vote")
    public Response<Void> createVote(@RequestBody @Valid VoteRequest.createVoteReq voteRequest) {

        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();

        voteRequest.setUserEmail(userEmail);
        voteService.createVote(voteRequest, userEmail);

        return Response.of(SuccessCode.VOTE_CREATED_OK);
    }

    @Validated
    @Operation(
            summary = "투표 조회 API",
            description = "여행 계획에 대한 투표 결과를 타입에 따라 구분하여 반환 합니다." +
                    "사용자가 투표한 Type 에 해당하는 항목에 사용자의 id 와 name 이 포함 되어 반환됩니다.")
    @GetMapping("/vote/results/{tripId}")
    public Response<List<VoteResponse.ResultDTO>> getVoteResults(@PathVariable @NotNull(message = "여행 ID는 필수입니다.") Long tripId) {

        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();

        List<VoteResponse.ResultDTO> results = voteService.getVoteResults(userEmail, tripId);

        return Response.of(SuccessCode.VOTE_RESULTS_OK, results);
    }

    @Validated
    @Operation(
            summary = "투표 종료 및 여행 계획 여부 판단 API",
            description = "투표의 종료 여부를 판단하고, 투표 결과에 따라 여행 계획 성공 및 실패 여부가 확정됩니다." )
    @PostMapping("/vote/trip-result")
    public Response<Void> validVoteResult(@RequestBody @Valid VoteRoomRequest voteRoomRequest) {
        boolean isVoteFailed  = validVoteResultService.validResult(voteRoomRequest);

        if (isVoteFailed) { return Response.of(ErrorCode.VOTE_RESULT_FAILED); }
        else { return Response.of(SuccessCode.VOTE_FINISH_OK); }
    }
}
