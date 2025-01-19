package com.umc.yeogi_gal_lae.api.vote.controller;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public Response<Long> createVote(@RequestBody VoteRequest voteRequest) {

        // 현재 인증된 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String userEmail;
        if (principal != null && principal.getClass().getName().equals("User")) {    // Principal 의 객체 타입 확인
            userEmail = ((UserDetails) principal).getUsername();
        }
        else if (principal != null) { userEmail = principal.toString();}
        else { throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다."); }

        voteRequest.setUserEmail(userEmail);
        voteService.createVote(voteRequest);

        Long VoteId = voteService.createVote(voteRequest);
        return Response.of(SuccessCode.CREATED, VoteId);
    }

    @Validated
    @Operation(
            summary = "투표 조회 API",
            description = "여행 계획에 대한 투표 결과를 타입에 따라 구분하여 반환 합니다." +
                          "사용자가 투표한 Type 에 해당하는 항목에 사용자의 id 와 name 이 포함 되어 반환됩니다.")
    @GetMapping("/vote/results/{tripId}")
    public Response<List<VoteResponse>> getVoteResults(@PathVariable @NotNull(message = "여행 ID는 필수입니다.") Long tripId) {

        // 현재 인증된 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String userEmail;
        if (principal != null && principal.getClass().getName().equals("User")) {    // Principal 의 객체 타입 확인
            userEmail = ((UserDetails) principal).getUsername();
        }
        else if (principal != null) { userEmail = principal.toString();}
        else { throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다."); }

        List<VoteResponse> response = voteService.getVoteResults(userEmail, tripId);
        return Response.of(SuccessCode.OK, response);
    }
}
