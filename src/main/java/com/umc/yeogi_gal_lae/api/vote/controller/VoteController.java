package com.umc.yeogi_gal_lae.api.vote.controller;

import com.umc.yeogi_gal_lae.api.vote.dto.VoteRequest;
import com.umc.yeogi_gal_lae.api.vote.dto.VoteResponse;
import com.umc.yeogi_gal_lae.api.vote.service.VoteService;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VoteController {

    @Autowired
    private final VoteService voteService;

    @Operation(summary = "투표 API", description = "특정 사용자의 투표 요청 입니다.")
    @PostMapping("/vote")
    public Response<Long> createVote(@RequestBody VoteRequest voteRequest) {

        Long VoteId = voteService.createVote(voteRequest);

        return Response.of(SuccessCode.CREATED, VoteId);
    }

    @Validated
    @Operation(
            summary = "투표 조회 API",
            description = "여행 계획에 대한 투표 결과를 타입에 따라 구분하여 반환 합니다." +
                          "사용자가 투표한 Type 에 해당하는 항목에 사용자의 id 와 name 이 포함 되어 반환됩니다.")
    @GetMapping("/vote/results")
    public Response<List<VoteResponse>> getVoteResults(
            @RequestParam @NotNull(message = "사용자 ID는 필수입니다.") Long userId,
            @RequestParam @NotNull(message = "여행 ID는 필수입니다.") Long tripId) {

        List<VoteResponse> response = voteService.getVoteResults(userId, tripId);

        return Response.of(SuccessCode.OK, response);
    }
}
