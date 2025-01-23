package com.umc.yeogi_gal_lae.api.AITripPlan.service;

import com.umc.yeogi_gal_lae.api.AITripPlan.dto.response.AITripPlanResponse;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.place.dto.response.PlaceResponse;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.global.common.OpenAIConfig;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AITripPlanService {

    private final RoomRepository roomRepository;
    private final OpenAIConfig openAIConfig;

    public AITripPlanService(RoomRepository roomRepository, OpenAIConfig openAIConfig) {
        this.roomRepository = roomRepository;
        this.openAIConfig = openAIConfig;
    }

    /**
     * 특정 Room에 속한 모든 Place를 기반으로 여행 일정 생성
     *
     * @param roomId Room의 ID
     * @return 생성된 여행 일정
     */
    @Transactional(readOnly = true)
    public AITripPlanResponse generateTripPlan(Long roomId) {
        // Room 존재 여부 확인
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        List<Place> places = room.getPlaces();

        if (places.isEmpty()) {
            throw new BusinessException(ErrorCode.NO_PLACES_FOUND);
        }

        // Place 정보를 문자열로 변환
        String placesInfo = places.stream()
                .map(place -> String.format("- %s: %s (Lat: %.4f, Lng: %.4f)", place.getPlaceName(), place.getAddress(),
                        place.getLatitude(), place.getLongitude()))
                .collect(Collectors.joining("\n"));

        // GPT에 보낼 프롬프트 작성
        String prompt = String.format(
                "다음 장소들을 포함하여 3일간의 여행 일정을 JSON 형식으로 작성해줘. 각 날마다 'day1', 'day2', 'day3'으로 나누고, 각 날마다 'places' 배열에 장소의 'placeId', 'placeName', 'address', 'lat', 'lng'를 포함해줘.\n\n%s",
                placesInfo
        );

        // OpenAIConfig를 통해 GPT API 호출
        String itineraryJson = openAIConfig.generateItinerary(prompt);

        // JSON 파싱
        AITripPlanResponse itineraryResponse = openAIConfig.parseItinerary(itineraryJson);

        // 응답의 장소가 실제 방의 장소와 일치하는지 검증
        validateItineraryResponse(itineraryResponse, places);

        return itineraryResponse;
    }

    /**
     * GPT가 생성한 일정의 장소가 실제 방의 장소와 일치하는지 검증
     *
     * @param itineraryResponse 생성된 여행 일정
     * @param places            실제 방에 속한 장소들
     */
    private void validateItineraryResponse(AITripPlanResponse itineraryResponse, List<Place> places) {
        Map<Long, Place> placeMap = places.stream()
                .collect(Collectors.toMap(Place::getId, place -> place));

        for (Map.Entry<String, AITripPlanResponse.Day> entry : itineraryResponse.getData().entrySet()) {
            AITripPlanResponse.Day day = entry.getValue();
            for (PlaceResponse placeResponse : day.getPlaces()) { // Place -> PlaceResponse로 수정
                Place place = placeMap.get(placeResponse.getPlaceId());
                if (place == null) {
                    throw new BusinessException(ErrorCode.AI_TRIP_PLAN_GENERATION_FAILED); // ErrorCode -> Code로 수정
                }
                if (!place.getPlaceName().equals(placeResponse.getPlaceName()) ||
                        !Objects.equals(place.getLatitude(), placeResponse.getLat()) ||
                        !Objects.equals(place.getLongitude(), placeResponse.getLng())) {
                    throw new BusinessException(ErrorCode.AI_TRIP_PLAN_GENERATION_FAILED); // ErrorCode -> Code로 수정
                }
            }
        }
    }

}
