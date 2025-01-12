package com.umc.yeogi_gal_lae.api.home.mapper;

import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.vote.domain.Vote;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HomeResponseMapper {

    public List<Map<String, Object>> mapVotesToResponse(List<Vote> votes) {
        return votes.stream().map(vote -> {
            Map<String, Object> voteInfo = new HashMap<>();
//            voteInfo.put("roomName", vote.getTripPlan().getName()); // 방이름 TODO -> 투표와 여행계획 연관관계 설정 필요
            voteInfo.put("location", "투표 장소"); // 장소 (데이터 필요 시 대체)
            voteInfo.put("completedParticipants", 1); // 투표 완료 인원 (예시 데이터)
            return voteInfo;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> mapActiveVoteRoomsToResponse(List<Map<String, Object>> rooms) {
        Map<String, Object> response = new HashMap<>();
        response.put("count", rooms.size());
        response.put("rooms", rooms);
        return response;
    }

    public Map<String, Object> mapPlannedTripsToResponse(List<TripPlan> plannedTrips) {
        List<Map<String, Object>> trips = plannedTrips.stream().map(trip -> {
            Map<String, Object> tripInfo = new HashMap<>();
            tripInfo.put("roomName", trip.getName()); // 방 이름
            tripInfo.put("location", trip.getLocation()); // 장소
            tripInfo.put("startTime", trip.getStartDate()); // 투표 시작 시간
            tripInfo.put("endTime", trip.getEndDate()); // 투표 종료 시간
            return tripInfo;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("count", plannedTrips.size());
        response.put("trips", trips);
        return response;
    }
}