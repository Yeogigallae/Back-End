package com.umc.yeogi_gal_lae.api.home.service;

import com.umc.yeogi_gal_lae.api.home.mapper.HomeResponseMapper;
import com.umc.yeogi_gal_lae.api.vote.domain.Vote;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final VoteRepository voteRepository;
    private final HomeResponseMapper homeResponseMapper;

    /**
     * 특정 사용자가 참여 중인 투표 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자가 참여한 투표 목록
     */
    public List<Map<String, Object>> getActiveVoteRooms(Long userId) {

        // 사용자가 참여한 투표 데이터를 가져옵니다.
        List<Vote> votes = voteRepository.findByUserId(userId);

        // 매퍼를 사용하여 응답 데이터로 변환
        return homeResponseMapper.mapVotesToResponse(votes);
    }
}