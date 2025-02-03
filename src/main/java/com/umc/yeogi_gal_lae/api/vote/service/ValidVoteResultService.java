package com.umc.yeogi_gal_lae.api.vote.service;

import com.umc.yeogi_gal_lae.api.notification.domain.NotificationType;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.types.Status;
import com.umc.yeogi_gal_lae.api.vote.domain.Vote;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteRoom;
import com.umc.yeogi_gal_lae.api.vote.domain.VoteType;
import com.umc.yeogi_gal_lae.api.vote.dto.request.VoteRoomRequest;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRepository;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRoomRepository;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umc.yeogi_gal_lae.api.notification.service.NotificationService;


import java.time.LocalDateTime;
import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class ValidVoteResultService {

    private final VoteRepository voteRepository;
    private final TripPlanRepository tripPlanRepository;
    private final RoomRepository roomRepository;
    private final VoteRoomRepository voteRoomRepository;
    private final NotificationService notificationService;

    @Transactional
    public boolean validResult(VoteRoomRequest voteRoomRequest) {

        // 투표 완료 여부 확인
        if (!checkVoteCompleted(voteRoomRequest)) { throw new BusinessException(ErrorCode.VOTE_NOT_COMPLETED_YET);  }

        VoteRoom voteRoom = findVoteRoomById(voteRoomRequest.getVoteRoomId());
        TripPlan tripPlan = voteRoom.getTripPlan();

        if (isVoteTimeExpired(voteRoom, tripPlan)) {
            voteRoomRepository.delete(voteRoom);
            return true;      // 재투표
        }

        // 찬성/반대 투표 집계
        List<Vote> votes = voteRepository.findAllVotesByTripPlanId(tripPlan.getId());
        long goodVotes = votes.stream().filter(v -> v.getType() == VoteType.GOOD).count();
        long badVotes = votes.stream().filter(v -> v.getType() == VoteType.BAD).count();


        // 반대표가 더 많을 시, 재투표를 위해 투표 방 삭제
        if (goodVotes < badVotes) {
            voteRoomRepository.delete(voteRoom);
            
            // roomId를 통해 roomName 가져오기
            Room room = findRoomById(voteRoomRequest.getRoomId());
            String roomName = room.getName();

            // 투표 완료 알림 생성
            notificationService.createEndNotification(roomName, NotificationType.VOTE_COMPLETE);
            return true;
        }
        else{
            tripPlan.setStatus(Status.COMPLETED);   // 여행 계획 '완료'로 상태 변경
            tripPlanRepository.save(tripPlan);

            // roomId를 통해 roomName 가져오기
            Room room = findRoomById(voteRoomRequest.getRoomId());
            String roomName = room.getName();

            // 투표 완료 알림 생성
            notificationService.createEndNotification(roomName, NotificationType.VOTE_COMPLETE);
            return false;
        }

    }

    @Transactional(readOnly = true)
    public boolean checkVoteCompleted(VoteRoomRequest voteRoomRequest) {

        // 반복되는 로직 헬퍼 클래스로 분리
        VoteRoom voteRoom = findVoteRoomById(voteRoomRequest.getVoteRoomId());
        TripPlan tripPlan = findTripPlanById(voteRoomRequest.getTripId());

        // 조건 1. 모든 멤버가 투표 했는지
        List<Vote> votes = voteRepository.findAllVotesByTripPlanId(tripPlan.getId());  // 여행에 해당하는 모든 투표 리스트
        boolean allMembersVoted = isAllMembersVoted(voteRoomRequest.getRoomId(), votes);

        // 조건 2. 투표 제한 시간 초과
        boolean isTimeExpired = isVoteTimeExpired(voteRoom, tripPlan);

        return isTimeExpired || allMembersVoted;
    }

    private TripPlan findTripPlanById(Long tripId) {
        return tripPlanRepository.findById(tripId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRIP_PLAN_NOT_FOUND));
    }

    private Room findRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
    }

    private VoteRoom findVoteRoomById(Long voteRoomId) {
        return voteRoomRepository.findById(voteRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VOTE_ROOM_NOT_FOUND));
    }

    private boolean isAllMembersVoted(Long roomId, List<Vote> votes) {
        Room room = findRoomById(roomId);
        return room.getRoomMembers().size() == votes.size();
    }

    private boolean isVoteTimeExpired(VoteRoom voteRoom, TripPlan tripPlan) {
        // 현재 시간이 투표 만료 시간보다 이후인지 확인 (투표방 생성 시간 = 투표 시작 시간)
        return LocalDateTime.now().isAfter(
                voteRoom.getCreatedAt().plusSeconds(tripPlan.getVoteLimitTime().getSeconds())
        );
    }
}
