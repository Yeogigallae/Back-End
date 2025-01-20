package com.umc.yeogi_gal_lae.api.vote.controller;

import com.umc.yeogi_gal_lae.api.vote.AuthenticatedUserUtils;
import com.umc.yeogi_gal_lae.api.vote.dto.VoteRequest;
import com.umc.yeogi_gal_lae.api.vote.dto.VoteResponse;
import com.umc.yeogi_gal_lae.api.vote.service.VoteService;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "투표 API", description = "현재 사용자의 투표 요청 입니다.")
    @PostMapping("/vote")
    public Response<Void> createVote(@RequestBody VoteRequest voteRequest) {

        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();

        voteRequest.setUserEmail(userEmail);
        voteService.createVote(voteRequest);
        return Response.of(SuccessCode.VOTE_CREATED_OK);
    }

    @Validated
    @Operation(
            summary = "투표 조회 API",
            description = "여행 계획에 대한 투표 결과를 타입에 따라 구분하여 반환 합니다." +
                          "사용자가 투표한 Type 에 해당하는 항목에 사용자의 id 와 name 이 포함 되어 반환됩니다.")
    @GetMapping("/vote/results/{tripId}")
    public Response<List<VoteResponse>> getVoteResults(@PathVariable @NotNull(message = "여행 ID는 필수입니다.") Long tripId) {

        String userEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();

        voteService.getVoteResults(userEmail, tripId);

        return Response.of(SuccessCode.VOTE_RESULTS_OK);
    }
}
