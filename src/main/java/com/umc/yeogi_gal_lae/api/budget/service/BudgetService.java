package com.umc.yeogi_gal_lae.api.budget.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.yeogi_gal_lae.api.budget.dto.BudgetResponse;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class BudgetService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String apiKey;

    public BudgetService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * GPT API에 전달할 프롬프트를 구성하고, 응답을 파싱하여 각 일차별 BudgetResponse 정보를 포함하는 Map을 반환합니다.
     *
     * @param itineraryMap 각 일차(예: "1일차", "2일차")에 해당하는 Place 목록
     * @return 각 일차별 BudgetResponse 정보가 담긴 Map
     */
    public Map<String, BudgetResponse> generateBudgetRecommendations(Map<String, List<Place>> itineraryMap) {
        String prompt = buildPrompt(itineraryMap);
        String gptResponse = callGptApi(prompt);
        return parseGptResponse(gptResponse);
    }

    private String buildPrompt(Map<String, List<Place>> itineraryMap) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("다음은 각 일차별 여행 일정입니다.\n");
        itineraryMap.forEach((day, places) -> {
            promptBuilder.append(day).append(":\n");
            for (Place p : places) {
                promptBuilder.append("- ").append(p.getPlaceName())
                        .append(" (").append(p.getAddress()).append(")\n");
            }
            promptBuilder.append("\n");
        });
        promptBuilder.append("위 일정 정보를 바탕으로 각 일차별로 식사비, 활동비, 쇼핑비, 교통비를 추천해줘.\n")
                .append("결과는 JSON 형식으로 출력해줘. 예시:\n")
                .append("{\n")
                .append("  \"1일차\": {\"mealBudget\": 20000, \"activityBudget\": 15000, \"shoppingBudget\": 10000, \"transportBudget\": 5000},\n")
                .append("  \"2일차\": {\"mealBudget\": 25000, \"activityBudget\": 12000, \"shoppingBudget\": 8000, \"transportBudget\": 6000}\n")
                .append("}\n");
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

    private Map<String, BudgetResponse> parseGptResponse(String gptResponse) {
        try {
            JsonNode root = objectMapper.readTree(gptResponse);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            TypeReference<Map<String, BudgetResponse>> typeRef = new TypeReference<>() {
            };
            return objectMapper.readValue(content, typeRef);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }
}
