package com.umc.yeogi_gal_lae.api.itinerary.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.yeogi_gal_lae.api.itinerary.domain.Itinerary;
import com.umc.yeogi_gal_lae.api.itinerary.repository.ItineraryRepository;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.place.repository.PlaceRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import jakarta.transaction.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    private final ItineraryRepository itineraryRepository;

    @Value("${openai.api.key}")
    private String apiKey;

    public ItineraryService(PlaceRepository placeRepository, ItineraryRepository itineraryRepository,
                            WebClient.Builder webClientBuilder) {
        this.placeRepository = placeRepository;
        this.itineraryRepository = itineraryRepository;

        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * TripPlan의 정보와 해당 방에 등록된 장소들을 기반으로 GPT API를 호출하여 총 여행 일수에 맞게 "1일차", "2일차", ... 의 일정(각 일차에 추천 장소 목록)을 생성합니다.
     *
     * @param tripPlan 여행 계획 엔티티
     * @return Map: key는 "1일차", "2일차" 등, value는 해당 일차에 추천할 Place 목록
     */
    @Transactional
    public Map<String, List<Place>> generateAndStoreItinerary(TripPlan tripPlan) {
        // 1. TripPlan이 속한 Room의 Place 조회
        List<Place> places = placeRepository.findByRoom(tripPlan.getRoom());
        if (places.isEmpty()) {
            return Collections.emptyMap();
        }

        // 2. GPT API에 전달할 프롬프트 구성
        String prompt = buildPrompt(tripPlan, places);
        // 3. GPT API 호출 (blocking 방식)
        String gptApiResponse = callGptApi(prompt);
        // 4. GPT 응답 파싱 (예: {"1일차": ["Place A", "Place B"], "2일차": ["Place C", "Place D"], ...})
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

        // 6. 저장할 데이터를 위해, 각 일차별로 Place 이름 목록으로 변환
        Map<String, List<String>> itineraryByName = new LinkedHashMap<>();
        for (Map.Entry<String, List<Place>> entry : itinerary.entrySet()) {
            List<String> placeNames = entry.getValue().stream()
                    .map(Place::getPlaceName)
                    .collect(Collectors.toList());
            itineraryByName.put(entry.getKey(), placeNames);
        }

        try {
            String itineraryJson = objectMapper.writeValueAsString(itineraryByName);
            Optional<Itinerary> existing = itineraryRepository.findByTripPlanId(tripPlan.getId());
            Itinerary itineraryEntity;
            if (existing.isPresent()) {
                // 기존 엔티티 업데이트: 새 JSON을 설정한 새 객체로 병합
                itineraryEntity = Itinerary.builder()
                        .id(existing.get().getId())
                        .tripPlan(tripPlan)
                        .itineraryJson(itineraryJson)
                        .build();
            } else {
                itineraryEntity = Itinerary.builder()
                        .tripPlan(tripPlan)
                        .itineraryJson(itineraryJson)
                        .build();
            }
            // save()가 트랜잭션 내에서 merge/update를 처리합니다.
            itineraryRepository.save(itineraryEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itinerary;
    }

    private String buildPrompt(TripPlan tripPlan, List<Place> places) {
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
                .append("일정은 '1일차', '2일차', ... '").append(totalDays)
                .append("일차'와 같이 나누고, 각 일차마다 추천할 장소들을 추천 순서대로 나열해줘.\n")
                .append("결과는 JSON 형식으로 출력해줘. 예시:\n")
                .append("{\"1일차\": [\"장소 A\", \"장소 B\"], \"2일차\": [\"장소 C\", \"장소 D\"]}");
        return promptBuilder.toString();
    }

    private String callGptApi(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4");
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        requestBody.put("messages", List.of(message));
        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private Map<String, List<String>> parseGptResponse(String gptResponse) {
        try {
            JsonNode root = objectMapper.readTree(gptResponse);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            // 만약 응답에 불필요한 텍스트가 있다면, 첫 번째 '{'부터 파싱
            int jsonStart = content.indexOf('{');
            if (jsonStart != -1) {
                content = content.substring(jsonStart);
            }
            return objectMapper.readValue(content, new TypeReference<Map<String, List<String>>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    private Place findPlaceByName(List<Place> places, String name) {
        return places.stream()
                .filter(p -> p.getPlaceName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Map<String, List<Place>> getStoredItinerary(TripPlan tripPlan) {
        Optional<Itinerary> itineraryOpt = itineraryRepository.findByTripPlanId(tripPlan.getId());
        if (itineraryOpt.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            String itineraryJson = itineraryOpt.get().getItineraryJson();
            // 저장된 JSON은 Map<String, List<String>> 형식(일차별 Place 이름 목록)이라고 가정
            Map<String, List<String>> itineraryByDay =
                    objectMapper.readValue(itineraryJson, new TypeReference<Map<String, List<String>>>() {
                    });
            // TripPlan의 Room에 등록된 Place 목록을 가져와, 이름으로 매칭
            List<Place> places = placeRepository.findByRoom(tripPlan.getRoom());
            Map<String, List<Place>> itinerary = new LinkedHashMap<>();
            for (Map.Entry<String, List<String>> entry : itineraryByDay.entrySet()) {
                List<Place> dayPlaces = entry.getValue().stream()
                        .map(name -> findPlaceByName(places, name))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                itinerary.put(entry.getKey(), dayPlaces);
            }
            return itinerary;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }
}
