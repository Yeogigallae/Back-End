package com.umc.yeogi_gal_lae.api.vote.service;

import com.umc.yeogi_gal_lae.api.vote.dto.VoteRequest;
import com.umc.yeogi_gal_lae.api.vote.dto.VoteResponse;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class VoteService {
    @Autowired
    private VoteRepository voteRepository;
    // private final UserRepository userRepository; // User 데이터 조회를 위한 Repository
    // private final TripPlanRepository tripPlanRepository;

    @Transactional
    public void createVote(VoteRequest request){

//        User user = userRepository.findById(request.getUserId())
//                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 UserId : " + request.getUserId()));
//
//        TripPlan tripPlan = tripPlanRepository.findById(request.getTripId())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 여행 계획 Id : " + request.getTripId()));
//        Vote vote = Vote.builder()
//                .user(user) // User 정보 연결
//                .tripPlan(tripPlan) // TripPlan 정보 연결
//                .type(Vote.VoteType.valueOf(request.getType().toUpperCase())) // 투표 타입 설정
//                .build();

//        voteRepository.save(vote);
    }

    public List<VoteResponse.VoteDTO> getVoteResults(Long userId, Long tripId){
//        List<Vote> votes = voteRepository.findVoteByUserAndTripPlan(userId, tripId);

        // userId 에 해당하는 사용자의 투표 데이터 필터링
//        Optional<Vote> userVote = votes.stream()
//                .filter(vote -> vote.getUser().getId().equals(userId))
//                .findFirst();

        // 투표 데이터를 'GOOD '과 'BAD' 로 그룹화
//        Map<String, List<Vote>> groupedVotes = votes.stream()
//                .collect(Collectors.groupingBy(vote -> vote.getType().name()));
//
//        List<Vote> goodVotes = groupedVotes.getOrDefault("GOOD", Collections.emptyList());
//        VoteResponse.VoteDTO goodResponse = VoteResponse.builder()
//                .id(userVote.isPresent() && userVote.get().getType().name().equals("GOOD")
//                        ? userVote.get().getUser().getId()
//                        : null) // 해당 사용자가 'good'에 투표했으면 ID 포함
//                .name(userVote.isPresent() && userVote.get().getType().name().equals("GOOD")
//                        ? userVote.get().getUser().getName()
//                        : null) // 해당 사용자가 'good'에 투표했으면 이름 포함
//                .type("GOOD") // 타입 설정
//                .count(groupedVotes.getOrDefault("GOOD", 0L).intValue())
//                .build();


        // 'BAD' 에 해당하는 투표 리스트 가져옴. 없으면 빈 리스트 반환
//        List<Vote> badVotes = groupedVotes.getOrDefault("BAD", Collections.emptyList());
//        VoteResponse.VoteDTO badResponse = VoteResponse.builder()
//                .id(userVote.isPresent() && userVote.get().getType().name().equals("BAD")
//                        ? userVote.get().getUser().getId()
//                        : null) // 해당 사용자가 'bad'에 투표했으면 ID 포함
//                .name(userVote.isPresent() && userVote.get().getType().name().equals("BAD")
//                        ? userVote.get().getUser().getName()
//                        : null) // 해당 사용자가 'bad'에 투표했으면 이름 포함
//                .type("BAD") // 타입 설정
//                .count(groupedVotes.getOrDefault("BAD", 0L).intValue())
//                .build();
//
//
//         return List.of(goodResponse, badResponse);
//    }

//}