package com.umc.yeogi_gal_lae.api.vote.service;

import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.api.vote.converter.VoteConverter;
import com.umc.yeogi_gal_lae.api.vote.domain.Vote;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteType;

import com.umc.yeogi_gal_lae.api.vote.dto.VoteRequest;
import com.umc.yeogi_gal_lae.api.vote.dto.VoteResponse;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final TripPlanRepository tripPlanRepository;

    @Transactional
    public Long createVote(VoteRequest request){

        User user = userRepository.findById(request.getUserId()).orElseThrow(()-> new IllegalArgumentException("User not found"));
        TripPlan tripPlan = tripPlanRepository.findById(request.getTripId()).orElseThrow(()-> new IllegalArgumentException("Trip not found"));

        Vote vote = voteRepository.findByTripPlanId(tripPlan.getId())      // DB 에 Vote 객체가 있다면,
                .orElseGet(() -> Vote.builder()         // 없다면, Vote 객체 생성
                        .tripPlan(tripPlan)
                        .type(VoteType.valueOf(request.getType().trim().toUpperCase()))   // 초기 타입 설정
                        .build());

        // 중복 투표 방지 (Entity 간의 관계 설정이기에, Entity 레벨에서 관리하도록 구현)
        // => JPA 가 변경 내용 자동 반영하므로 Repository 불필요
        if(user.getVote()==null || !user.getVote().equals(vote)){
            user.setVote(vote);
            userRepository.save(user);     // 변경 사항 저장
        }

        return voteRepository.save(vote).getId();
    }

    public List<VoteResponse> getVoteResults(Long userId, Long tripId){

        if (!userRepository.existsById(userId)) {throw new EntityNotFoundException("사용자를 찾을 수 없습니다."); }
        if (!tripPlanRepository.existsById(tripId)) { throw new EntityNotFoundException("여행 계획을 찾을 수 없습니다.");}

        // 투표에 해당하는 여행 계획에 포함된 '모든' User 정보 배열에 담기
        List<User> users = userRepository.findUsersByVoteTripPlanId(tripId);

        // 현재 접속한 사용자에 대한 투표 데이터
        Optional<User> userVote = users.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();

        // 투표 데이터를 type 이름 기준('GOOD ','BAD')으로 그룹화, 타입 당 투표 수 계산
        // {"GOOD": 3, "BAD": 2}
        Map<String, Long> groupedVotes = users.stream()
                .filter(user -> user.getVote() != null)      // 투표한 데이터만 카운팅
                .map(user -> user.getVote().getType().name())
                .collect(Collectors.groupingBy(
                        typeName -> typeName,
                        Collectors.counting())
                );


        VoteResponse goodResponse = VoteConverter.convert("GOOD", userVote.orElse(null), groupedVotes);
        VoteResponse badResponse = VoteConverter.convert("BAD", userVote.orElse(null), groupedVotes);

        return List.of(goodResponse, badResponse);
    }
}