package com.umc.yeogi_gal_lae.api.home.service;

import com.umc.yeogi_gal_lae.api.vote.dto.VoteResponse;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeService {

    private final VoteRepository voteRepository;
        /**
     * 특정 사용자가 참여 중인 투표 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자가 참여한 투표 목록
     */
    public List<VoteResponse.VoteDTO> getUserVotes(Long userId) {
        // 사용자가 참여한 투표 데이터를 가져옵니다.
        List<Vote> votes = voteRepository.findByUserId(userId);

        // Vote 데이터를 VoteDTO로 변환하여 반환
        return votes.stream()
                .map(vote -> VoteResponse.VoteDTO.builder()
                        .id(vote.getId().toString())
                        .name(vote.getTripPlan().getName()) // 관련 여행 계획 이름
                        .type(vote.getType().name())       // 투표 타입 (GOOD/BAD)
                        .count(1) // 현재 사용자의 투표 수는 1로 고정
                        .build())
                .collect(Collectors.toList());
    }
}
