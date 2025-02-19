package com.umc.yeogi_gal_lae.api.vote.service;

import com.umc.yeogi_gal_lae.api.notification.domain.NotificationType;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.repository.RoomMemberRepository;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.api.vote.converter.VoteConverter;
import com.umc.yeogi_gal_lae.api.vote.domain.Vote;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteRoom;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteType;
import com.umc.yeogi_gal_lae.api.notification.service.NotificationService;
import org.redisson.api.RedissonClient;


import com.umc.yeogi_gal_lae.api.vote.dto.request.VoteRequest;
import com.umc.yeogi_gal_lae.api.vote.dto.VoteResponse;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRepository;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRoomRepository;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.client.RedisException;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.umc.yeogi_gal_lae.global.error.ErrorCode.*;

@Service
@AllArgsConstructor
@Slf4j
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final TripPlanRepository tripPlanRepository;
    private final VoteRoomRepository voteRoomRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;
    private final RoomMemberRepository roomMemberRepository;

    private RedissonClient redissonClient;
    private CacheManager cacheManager;

    @Transactional(readOnly = true)
    public VoteResponse.VoteInfoDTO getTripPlanInfoForVote(Long tripId, Long roomId , String userEmail){

        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new BusinessException(ROOM_NOT_FOUND));
        TripPlan tripPlan = tripPlanRepository.findById(tripId).orElseThrow(() -> new BusinessException(TRIP_PLAN_NOT_FOUND));

        int userCount = roomMemberRepository.countByRoomIdAndTripId(roomId, tripId);

        return VoteConverter.toResponse(user, room, tripPlan, userCount);
    }


    @Transactional
    public void createVote(VoteRequest.createVoteReq request, String userEmail){

        User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));
        TripPlan tripPlan = tripPlanRepository.findById(request.getTripId()).orElseThrow(()-> new BusinessException(ErrorCode.TRIP_PLAN_NOT_FOUND));
        VoteRoom voteRoom = voteRoomRepository.findByTripPlanId(tripPlan.getId()).orElseThrow(() -> new BusinessException(VOTE_ROOM_NOT_FOUND));
        VoteType voteType = VoteType.valueOf(request.getType().trim().toUpperCase());

        // Key 기반의 분산 락을 적용하므로써, 사용자의 동시 투표 방지
        RLock lock = redissonClient.getLock("voteLock:" + userEmail);
        boolean isLocked = false;
        try{
            isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
            log.info("락 획득 여부: {}, 현재 스레드가 락을 보유하고 있는가? {}", isLocked, lock.isHeldByCurrentThread());
            if (!isLocked) { throw new BusinessException(ErrorCode.VOTE_CONCURRENT_UPDATE);}

            // 락 획득 성공 시, 레디스에 저장된 투표 데이터를 확인하여 캐싱된 데이터가 있을 시, DB 조회 x
            Vote vote = getCachedVoteByTripPlan(tripPlan.getId());
            if (vote == null) {
                vote = voteRepository.save(Vote.builder()
                        .tripPlan(tripPlan)
                        .voteRoom(voteRoom)
                        .type(voteType)
                        .build());

                // 새로 생성된 경우 캐싱
                Vote finalVote = vote;
                Optional.ofNullable(cacheManager.getCache("votes"))
                        .ifPresent(cache -> cache.put(tripPlan.getId(), finalVote));
            }

            // 투표 시작 알림 생성
            notificationService.createStartNotification(tripPlan.getRoom().getName(), user.getUsername(), user.getEmail(),NotificationType.VOTE_START, tripPlan.getId(), tripPlan.getTripPlanType());

            // 기존 투표 이력 확인
            Vote currentVote = user.getVote();
            if (currentVote == null) {
                user.setVote(vote);
            }
            else if (currentVote.getTripPlan().getId().equals(tripPlan.getId())) {
                if (Thread.currentThread().isInterrupted()) {
                    log.warn("BusinessException 발생 후 스레드가 인터럽트된 상태 - 사용자: {}", userEmail);
                    Thread.interrupted();      // 인터럽트 상태 초기화
                }
                if (currentVote.getType().equals(voteType)) { throw new BusinessException(ErrorCode.DUPLICATE_VOTE_NOT_ALLOWED);}

                currentVote.setType(voteType);
                voteRepository.save(currentVote);

                // 사용자가 이미 투표한 내역을 변경할 경우, 레디스에 저장된 데이터를 삭제하여 최신화
                Optional.ofNullable(cacheManager.getCache("votes"))
                        .ifPresent(cache -> cache.evict(tripPlan.getId()));
            } else {
                user.setVote(vote);
            }
            userRepository.save(user);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.VOTE_CONCURRENT_UPDATE);
        } catch (Exception e) {
            log.error("예외 발생 함. 사용자: {} - 예외 타입: {}", userEmail, e.getClass().getName(), e);
            throw e;
        }finally {
            log.info("현재 스레드 사용자 {} 에 대한 락을 해제합니다.", userEmail);
            if (isLocked && lock.isHeldByCurrentThread()) { lock.unlock(); }
        }
    }

    public List<VoteResponse.ResultDTO> getVoteResults(String userEmail, Long tripId){

        Long userId = userRepository.findByEmail(userEmail).orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND)).getId();
        if (!tripPlanRepository.existsById(tripId)) { throw new BusinessException(TRIP_PLAN_NOT_FOUND);}

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

    // 캐싱 공간 및 키 할당
    @Cacheable(value = "votes", key = "#tripPlanId")
    public Vote getCachedVoteByTripPlan(Long tripPlanId) {
        return voteRepository.findByTripPlanId(tripPlanId).orElse(null);
    }

}