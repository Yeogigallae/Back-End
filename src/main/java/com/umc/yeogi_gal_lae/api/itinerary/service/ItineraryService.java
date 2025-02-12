package com.umc.yeogi_gal_lae.api.itinerary.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.place.repository.PlaceRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ItineraryService {

    private final PlaceRepository placeRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String apiKey;

    public ItineraryService(PlaceRepository placeRepository, WebClient.Builder webClientBuilder) {
        this.placeRepository = placeRepository;
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * TripPlan의 정보와 해당 방에 등록된 장소들을 기반으로 GPT API를 호출하여 총 여행 일수에 맞게 "1일차", "2일차", ... 의 일정(각 일차에 추천 장소 목록)을 생성합니다.
     *
     * @param tripPlan 여행 계획 엔티티
     * @return Map: key는 "1일차", "2일차" 등, value는 해당 일차에 추천할 Place 목록
     */
    public Map<String, List<Place>> generateItinerary(TripPlan tripPlan) {
        // 1. TripPlan이 속한 방(Room)의 모든 Place 조회
        List<Place> places = placeRepository.findByRoom(tripPlan.getRoom());
        if (places.isEmpty()) {
            return Collections.emptyMap();
        }

        // 2. GPT API에 전달할 프롬프트 구성 (여행 일수를 포함하여 요청)
        String prompt = buildPrompt(tripPlan, places);

        // 3. GPT API 호출 (blocking 방식 - 실제 서비스에서는 비동기 처리를 고려)
        String gptApiResponse = callGptApi(prompt);

        // 4. GPT 응답 파싱: JSON 문자열 (예: {"1일차": ["Place A", "Place B"], "2일차": ["Place C", "Place D"], ...})
        Map<String, List<String>> itineraryByDay = parseGptResponse(gptApiResponse);

        // 5. 응답의 장소 이름을 실제 Place 객체와 매핑
        Map<String, List<Place>> itinerary = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : itineraryByDay.entrySet()) {
            String dayLabel = entry.getKey();
            List<Place> dayPlaces = entry.getValue().stream()
                    .map(name -> findPlaceByName(places, name))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            itinerary.put(dayLabel, dayPlaces);
        }
        return itinerary;
    }

    /**
     * GPT API에 전달할 프롬프트를 생성합니다. 여행 시작일과 종료일을 기반으로 총 여행 일수를 계산하고, 해당 일수에 맞게 일정을 요청합니다.
     */
    private String buildPrompt(TripPlan tripPlan, List<Place> places) {
        // 여행 총 일수 계산 (시작일과 종료일 포함)
        long totalDays = ChronoUnit.DAYS.between(tripPlan.getStartDate(), tripPlan.getEndDate()) + 1;

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("다음 여행 정보를 기반으로 효율적인 일정을 생성해줘.\n")
                .append("여행 시작일: ").append(tripPlan.getStartDate()).append("\n")
                .append("여행 종료일: ").append(tripPlan.getEndDate()).append("\n")
                .append("총 여행 일수: ").append(totalDays).append("일\n")
                .append("여행 지역: ").append(tripPlan.getLocation()).append("\n\n")
                .append("다음은 방문 가능한 장소 목록 (이름, 주소, 위도, 경도)야:\n");
        for (Place p : places) {
            promptBuilder.append("- ").append(p.getPlaceName())
                    .append(" (주소: ").append(p.getAddress())
                    .append(", 좌표: (").append(p.getLatitude()).append(", ")
                    .append(p.getLongitude()).append("))\n");
        }
        promptBuilder.append("\n")
                .append("위 정보를 바탕으로 총 ").append(totalDays)
                .append("일의 여행 일정(각 일차에 방문할 장소 추천)을 생성해줘.\n")
                .append("일정은 '1일차', '2일차', ... '").append(totalDays).append("일차'와 같이 나누고, ")
                .append("각 일차마다 추천할 장소들을 추천 순서대로 나열해줘.\n")
                .append("결과는 JSON 포맷으로 출력해줘. 예시:\n")
                .append("{\"1일차\": [\"장소 A\", \"장소 B\"], \"2일차\": [\"장소 C\", \"장소 D\"], ...}");
        return promptBuilder.toString();
    }

    /**
     * WebClient를 이용해 GPT API를 호출하고 응답 문자열을 반환합니다.
     */
    private String callGptApi(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4");
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        requestBody.put("messages", messages);

        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * GPT API의 응답(JSON 문자열)을 파싱하여, 일자별 추천 장소 이름 목록을 반환합니다.
     */
    private Map<String, List<String>> parseGptResponse(String gptApiResponse) {
        try {
            JsonNode root = objectMapper.readTree(gptApiResponse);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            return objectMapper.readValue(content, new TypeReference<Map<String, List<String>>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    /**
     * 장소 이름을 기준으로 Place 객체를 조회합니다.
     */
    private Place findPlaceByName(List<Place> places, String name) {
        return places.stream()
                .filter(p -> p.getPlaceName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
