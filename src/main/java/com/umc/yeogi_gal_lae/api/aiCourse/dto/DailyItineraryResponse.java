package com.umc.yeogi_gal_lae.api.aiCourse.dto;


import com.umc.yeogi_gal_lae.api.place.dto.response.PlaceResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyItineraryResponse {
    // 예: "1일차", "2일차" 등의 문자열
    private String roomName;
    private int totalRoomMember;
    private String day;
    private List<PlaceResponse> places;
}
