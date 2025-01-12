package com.umc.yeogi_gal_lae.api.vote.service;

import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
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
    public void createVote(VoteRequest request){

        User user = userRepository.findById(request.getUserId()).orElse(null);
        TripPlan tripPlan = tripPlanRepository.findById(request.getTripId()).orElse(null);

        Vote vote = voteRepository.findByTripPlanId(Objects.requireNonNull(tripPlan).getId())      // DB 에 Vote 객체가 있다면,
                .orElseGet(() -> Vote.builder()         // 없다면, Vote 객체 생성
                        .tripPlan(tripPlan)
                        .type(VoteType.valueOf(request.getType().trim().toUpperCase()))   // 초기 타입 설정
                        .users(new ArrayList<>())     // 사용자 리스트 생성
                        .build());

        // 중복 투표 방지 (Entity 간의 관계 설정이기에, Entity 레벨에서 관리하도록 구현)
        // => JPA 가 변경 내용 자동 반영하므로 Repository 언급 불필요
        if(!vote.getUsers().contains(user)){
            vote.getUsers().add(user);
            Objects.requireNonNull(user).setVote(vote);
        }

        voteRepository.save(vote);
    }

    public VoteResponse getVoteResults(Long userId, Long tripId){

        if (!userRepository.existsById(userId)) {throw new EntityNotFoundException("사용자를 찾을 수 없습니다."); }
        if (!tripPlanRepository.existsById(tripId)) { throw new EntityNotFoundException("여행 계획을 찾을 수 없습니다.");}

        List<Vote> votes = voteRepository.findVoteByUserAndTripPlan(userId, tripId);  // 특정 여행 계획의 모든 투표 조회

        // 위의 결과(votes) 입력 받은 userId 와 같은 것 필터링 => 현재 접속한 사용자에 대한 투표 데이터
        // 현재 접속자의 id 값만 필요하기에, findFirst 사용
        Optional<User> userVote = votes.stream()
                .flatMap(vote -> vote.getUsers().stream())   // 2차원 배열 -> 단일 원소 배열 ( [Vote[User]] )
                .filter(user -> user.getId().equals(userId))
                .findFirst();

        // 투표 데이터를 type 이름 기준('GOOD ','BAD')으로 그룹화, 타입 당 투표 수 계산
        Map<String, Long> groupedVotes = votes.stream()
                .collect(Collectors.groupingBy(
                        vote -> vote.getType().name(),
                        Collectors.counting())
                );

        // 'GOOD' 타입 응답 (없으면 null)
        VoteResponse.VoteDTO goodResponse = VoteResponse.VoteDTO.builder()
                .userId( userVote.filter(user -> user.getVote().getType().name().equals("GOOD"))
                        .map(User :: getId)    // 현재 사용자가 'GOOD' 에 투표했으면 ID 포함
                        .orElse(null))
                .userName(userVote.filter(user -> user.getVote().getType().name().equals("GOOD"))
                        .map(User :: getUsername)   // 현재 사용자가 'GOOD' 에 투표했으면 Name 포함
                        .orElse(null))
                .type("GOOD")
                .count(groupedVotes.getOrDefault("GOOD", 0L).intValue())  // 정수로 변환하여 저장
                .build();


        // 'BAD' 타입 응답 (없으면 null)
        VoteResponse.VoteDTO badResponse = VoteResponse.VoteDTO.builder()
                .userId(userVote.filter(user -> user.getVote().getType().name().equals("BAD"))
                        .map(User :: getId)    // 현재 사용자가 'BAD' 에 투표했으면 ID 포함
                        .orElse(null))
                .userName(userVote.filter(user -> user.getVote().getType().name().equals("BAD"))
                        .map(User :: getUsername)   // 현재 사용자가 'BAD' 에 투표했으면 Name 포함
                        .orElse(null))
                .type("BAD")
                .count(groupedVotes.getOrDefault("BAD", 0L).intValue())
                .build();

        return VoteResponse.builder()
                .code("SUCCESS")
                .message("투표 결과를 성공적으로 조회했습니다.")
                .data(List.of(goodResponse, badResponse))
                .build();
    }
}