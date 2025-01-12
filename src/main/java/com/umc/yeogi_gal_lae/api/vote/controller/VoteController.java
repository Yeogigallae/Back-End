package com.umc.yeogi_gal_lae.api.vote.controller;

import com.umc.yeogi_gal_lae.api.vote.dto.VoteRequest;
import com.umc.yeogi_gal_lae.api.vote.dto.VoteResponse;
import com.umc.yeogi_gal_lae.api.vote.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VoteController {

    @Autowired
    private final VoteService voteService;

    @Operation(summary = "투표 API", description = "특정 User의 투표 요청")
    @PostMapping("/vote")
    public void createVote(@RequestBody VoteRequest voteRequest) {
        voteService.createVote(voteRequest);
    }

    @Operation(summary = "투표 조회 API", description = "특정 TripPlan에 대한 투표 결과와 투표한 User 의 정보를 포함하여 반환")
    @GetMapping("/vote/results")
    public VoteResponse getVoteResults(@RequestParam Long userId, @RequestParam Long tripId) {

        List<VoteResponse.VoteDTO> voteResults = voteService.getVoteResults(userId, tripId);

        return VoteResponse.builder()
                .code("SUCCESS")
                .message("투표 결과를 성공적으로 조회했습니다.")
                .data(voteResults)
                .build();
    }
}