package com.umc.yeogi_gal_lae.global.config;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.yeogi_gal_lae.api.aiTripPlan.dto.request.ChatCompletionRequest;
import com.umc.yeogi_gal_lae.api.aiTripPlan.dto.response.AITripPlanResponse;
import com.umc.yeogi_gal_lae.api.aiTripPlan.dto.response.ChatCompletionResponse;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class OpenAIConfig {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIConfig.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public OpenAIConfig(
            @Value("${openai.api.url}") String openaiApiBaseUrl, // e.g. "https://api.openai.com/v1"
            @Value("${openai.api.key}") String openaiApiKey,
            ObjectMapper objectMapper
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(openaiApiBaseUrl)
                .defaultHeader("Authorization", "Bearer " + openaiApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.objectMapper = objectMapper;
    }

    /**
     * ChatGPT API (/v1/chat/completions) 호출하여 여행 일정을 생성
     *
     * @param prompt GPT에게 보낼 사용자 프롬프트
     * @return ChatGPT가 만들어낸 'JSON 형태'의 문자열
     */
    public String generateItinerary(String prompt) {
        // 1) ChatCompletionRequest 생성 (gpt-3.5-turbo 예시)
        ChatCompletionRequest requestBody = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo") // or "gpt-4" if you have access
                .messages(List.of(
                        ChatCompletionRequest.Message.builder()
                                .role("system")
                                .content("당신은 여행 일정 생성 전문가입니다. JSON 코드 블록 없이 순수 JSON으로만 응답해주세요.")
                                .build(),
                        ChatCompletionRequest.Message.builder()
                                .role("user")
                                .content(prompt)
                                .build()
                ))
                .max_tokens(500)
                .temperature(0.7)
                .build();

        // 2) WebClient로 /chat/completions 엔드포인트 호출
        ChatCompletionResponse response = webClient.post()
                .uri("/chat/completions")
                .body(Mono.just(requestBody), ChatCompletionRequest.class)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .block();

        // 3) 응답 검사
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            logger.error("OpenAI 응답이 비어 있습니다.");
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 4) ChatGPT가 생성한 'JSON 텍스트'
        String itineraryJson = response.getChoices().get(0).getMessage().getContent();
        logger.info("OpenAI 원본 응답: {}", itineraryJson);

        // 5) 필요 시, 백틱(```) 제거 (ChatGPT가 코드블록을 붙이는 경우 대응)
        String cleanedJson = cleanBackticks(itineraryJson);
        logger.info("백틱 제거 후 JSON: {}", cleanedJson);

        return cleanedJson;
    }

    /**
     * 여행 일정 JSON 문자열을 AITripPlanResponse DTO로 변환
     */
    public AITripPlanResponse parseItinerary(String itineraryJson) {
        try {
            // leading zeros 허용
            ObjectMapper customMapper = objectMapper.copy();
            customMapper.enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature());

            // JSON → DTO 역직렬화
            return customMapper.readValue(itineraryJson, AITripPlanResponse.class);
        } catch (Exception e) {
            logger.error("여행 일정 JSON 파싱 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 백틱(```)이나 ```json 으로 감싸져 있을 경우 제거
     *
     * @param raw 응답 문자열
     * @return 백틱 제거된 문자열
     */
    private String cleanBackticks(String raw) {
        // 1) trim
        String trimmed = raw.trim();
        // 2) 정규식으로 시작 ```(json)? + 공백, 끝나는 ``` 제거
        String cleaned = trimmed
                .replaceAll("^```(json)?\\s*", "")   // 예: ```json or ``` 제거
                .replaceAll("\\s*```$", "");        // 끝쪽 ``` 제거
        return cleaned;
    }
}
