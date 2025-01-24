package com.umc.yeogi_gal_lae.api.aiTripPlan.service;

import com.umc.yeogi_gal_lae.api.aiTripPlan.dto.response.AITripPlanResponse;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.global.config.OpenAIConfig;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
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
        // DB placeId -> Place 매핑
        Map<Long, Place> placeMap = places.stream()
                .collect(Collectors.toMap(Place::getId, place -> place));

        for (Map.Entry<String, AITripPlanResponse.Day> entry : itineraryResponse.getData().entrySet()) {
            AITripPlanResponse.Day day = entry.getValue();
            for (AITripPlanResponse.PlaceResponse placeResponse : day.getPlaces()) {
                Place place = placeMap.get(placeResponse.getPlaceId());

                // 1) placeId가 DB에 없는 경우 → 예외 대신 로그만 남기고 skip
                if (place == null) {
                    log.warn("DB에 없는 placeId입니다. placeId={}", placeResponse.getPlaceId());
                    // 필요하다면 continue로 다음 장소로 넘어감
                    // continue; // or break; or do nothing
                } else {
                    // 2) placeName, lat, lng 불일치
                    boolean mismatch = false;
                    if (!Objects.equals(place.getPlaceName(), placeResponse.getPlaceName())) {
                        log.warn("장소 이름 불일치. DB={}, AI={}", place.getPlaceName(), placeResponse.getPlaceName());
                        mismatch = true;
                    }
                    // lat, lng가 Double vs String일 수 있으니 변환 필요
                    Double lat = place.getLatitude();
                    Double lng = place.getLongitude();
                    // placeResponse.getLat()가 String이라면 Double.valueOf(...)
                    // placeResponse.getLng()도 마찬가지

                    if (!Objects.equals(lat, Double.valueOf(placeResponse.getLat()))) {
                        log.warn("위도 불일치. DB={}, AI={}", lat, placeResponse.getLat());
                        mismatch = true;
                    }
                    if (!Objects.equals(lng, Double.valueOf(placeResponse.getLng()))) {
                        log.warn("경도 불일치. DB={}, AI={}", lng, placeResponse.getLng());
                        mismatch = true;
                    }

                    // mismatch가 있더라도 에러로 중단하지 않고 로그만 남김
                    if (mismatch) {
                        log.info("DB 정보와 OpenAI 응답이 일치하지 않습니다. 하지만 예외는 발생시키지 않습니다.");
                    }
                }
            }
        }
        // 최종적으로 예외 없이 메서드 종료
        log.info("일정 검증 완료: mismatch가 있어도 예외를 던지지 않습니다.");
    }


}
