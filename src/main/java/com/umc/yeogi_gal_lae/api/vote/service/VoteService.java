package com.umc.yeogi_gal_lae.api.vote.service;

import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.types.Status;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.api.vote.converter.VoteConverter;
import com.umc.yeogi_gal_lae.api.vote.domain.Vote;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteRoom;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteType;

import com.umc.yeogi_gal_lae.api.vote.dto.request.VoteRequest;
import com.umc.yeogi_gal_lae.api.vote.dto.VoteResponse;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRepository;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRoomRepository;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final TripPlanRepository tripPlanRepository;
    private final VoteRoomRepository voteRoomRepository;

    @Transactional
    public synchronized void createVoteRoom(VoteRequest.createVoteRoomReq request) {
        // 1. 여행 계획 ID로 TripPlan 조회
        TripPlan tripPlan = tripPlanRepository.findById(request.getTripId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TRIP_PLAN_NOT_FOUND));

        // 2. 기존 VoteRoom이 존재하는지 확인 (중복 생성 방지)
        if (voteRoomRepository.findByTripPlanId(tripPlan.getId()).isPresent()) {
            throw new BusinessException(ErrorCode.VOTE_ROOM_ALREADY_EXISTS);
        }

        // 3. 새로운 VoteRoom 생성 및 저장
        VoteRoom voteRoom = new VoteRoom();
        voteRoom.setTripPlan(tripPlan);
        tripPlan.setStatus(Status.ONGOING); // 여행 계획을 '진행 중' 상태로 변경

        voteRoomRepository.save(voteRoom);
        tripPlanRepository.save(tripPlan);
    }

    @Transactional
    public void createVote(VoteRequest.createVoteReq request, String userEmail){

        // 유저 이메일로 검증
        User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));
        TripPlan tripPlan = tripPlanRepository.findById(request.getTripId()).orElseThrow(()-> new BusinessException(ErrorCode.TRIP_PLAN_NOT_FOUND));
        VoteRoom voteRoom = voteRoomRepository.findByTripPlanId(tripPlan.getId()).orElseThrow(() -> new BusinessException(ErrorCode.VOTE_ROOM_NOT_FOUND));


        Vote vote = voteRepository.findByTripPlanId(tripPlan.getId())      // DB 에 Vote 객체가 있다면,
                .orElseGet(() -> voteRepository.save(Vote.builder()         // 없다면, Vote 객체 생성
                        .tripPlan(tripPlan)
                        .voteRoom(voteRoom)
                        .type(VoteType.valueOf(request.getType().trim().toUpperCase()))   // 초기 타입 설정
                        .build()));

        // 기존 투표 이력 확인
        Vote currentVote = user.getVote();

        if (currentVote == null) { user.setVote(vote); }
        else if (currentVote.getTripPlan().getId().equals(tripPlan.getId())) {
            VoteType requestedType = VoteType.valueOf(request.getType().trim().toUpperCase());

            if (currentVote.getType().equals(requestedType)) { throw new IllegalArgumentException("같은 타입으로 중복 투표는 불가능합니다.");}

            currentVote.setType(requestedType);
            voteRepository.save(currentVote);
        } else {
            user.setVote(vote);
        }

        userRepository.save(user);
    }

    public List<VoteResponse.ResultDTO> getVoteResults(String userEmail, Long tripId){

        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new BusinessException.UserNotFoundException("요청하신 이메일과 일치하는 유저가 존재하지 않습니다."))
                .getId();

        if (!tripPlanRepository.existsById(tripId)) { throw new EntityNotFoundException("여행 계획을 찾을 수 없습니다.");}


        List<User> users = userRepository.findUsersByVoteTripPlanId(tripId);

        // 현재 접속한 사용자에 대한 투표 데이터
        Optional<User> userVote = users.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();

        // 투표 데이터를 type 이름 기준('GOOD ','BAD')으로 그룹화, 타입 당 투표 수 계산    // {"GOOD": 3, "BAD": 2}
        Map<String, Long> groupedVotes = users.stream()
                .filter(user -> user.getVote() != null && user.getVote().getType() != null)      // 투표한 데이터만 카운팅
                .map(user -> user.getVote().getType().name())
                .collect(Collectors.groupingBy(
                        typeName -> typeName,
                        Collectors.counting())
                );

        VoteResponse.ResultDTO goodResponse = VoteConverter.convert("GOOD", userVote.orElse(null), groupedVotes);
        VoteResponse.ResultDTO badResponse = VoteConverter.convert("BAD", userVote.orElse(null), groupedVotes);

        return List.of(goodResponse, badResponse);
    }
}