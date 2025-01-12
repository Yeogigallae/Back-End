package com.umc.yeogi_gal_lae.api.home.service;

import com.umc.yeogi_gal_lae.api.home.mapper.HomeResponseMapper;
import com.umc.yeogi_gal_lae.api.vote.domain.Vote;
import com.umc.yeogi_gal_lae.api.vote.dto.VoteResponse;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final VoteRepository voteRepository;
    private final HomeResponseMapper homeResponseMapper;

    /**
     =======
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
    public List<Map<String, Object>> getActiveVoteRooms(Long userId) {

        // 사용자가 참여한 투표 데이터를 가져옵니다.
        List<Vote> votes = voteRepository.findByUserId(userId);

        // 매퍼를 사용하여 응답 데이터로 변환
        return homeResponseMapper.mapVotesToResponse(votes);
    }

    // [필독] VoteResponse 말고, home 디렉토리 내부에 다른 이름으로 DTO 만들어서 사용해주세요!
//    public List<VoteResponse.VoteDTO> getUserVotes(Long userId) {
//        // 사용자가 참여한 투표 데이터를 가져옵니다.
//        List<Vote> votes = voteRepository.findByUserId(userId);
//
//        // Vote 데이터를 VoteDTO로 변환하여 반환
//        return votes.stream()
//                .map(vote -> VoteResponse.VoteDTO.builder()
//                        .id(vote.getId().toString())
//                        .name(vote.getTripPlan().getName()) // 관련 여행 계획 이름
//                        .type(vote.getType().name())       // 투표 타입 (GOOD/BAD)
//                        .count(1) // 현재 사용자의 투표 수는 1로 고정
//                        .build())
//                .collect(Collectors.toList());
//    }
//
//    public List<VoteResponse.VoteDTO> getUserVotes(Long userId) {
//        // 데이터베이스나 다른 외부 서비스에서 사용자 투표 데이터 가져오기
//        List<Vote> votes = voteRepository.findByUserId(userId);
//
//        // 데이터 가공 로직
//        return votes.stream()
//                .map(vote -> new VoteResponse.VoteDTO(vote.getId(), vote.getName(), vote.getLocation(), vote.getCount()))
//                .collect(Collectors.toList());
//    }
}
