package com.umc.yeogi_gal_lae.global.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.yeogi_gal_lae.api.AITripPlan.dto.request.OpenAIRequest;
import com.umc.yeogi_gal_lae.api.AITripPlan.dto.response.AITripPlanResponse;
import com.umc.yeogi_gal_lae.api.AITripPlan.dto.response.OpenAIResponse;
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

    public OpenAIConfig(@Value("${openai.api.url}") String openaiApiUrl,
                        @Value("${openai.api.key}") String openaiApiKey,
                        ObjectMapper objectMapper) {
        this.webClient = WebClient.builder()
                .baseUrl(openaiApiUrl)
                .defaultHeader("Authorization", "Bearer " + openaiApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.objectMapper = objectMapper;
    }

    /**
     * OpenAI GPT API를 호출하여 여행 일정을 생성합니다.
     *
     * @param prompt GPT에 보낼 프롬프트
     * @return 생성된 여행 일정 JSON 문자열
     */
    public String generateItinerary(String prompt) {
        OpenAIRequest openAIRequest = new OpenAIRequest();
        openAIRequest.setModel("gpt-3.5-turbo"); // 필요에 따라 모델 변경 가능
        openAIRequest.setMessages(List.of(
                new OpenAIRequest.Message("system", "당신은 여행 일정 생성 전문가입니다."),
                new OpenAIRequest.Message("user", prompt)
        ));

        OpenAIResponse openAIResponse = webClient.post()
                .body(Mono.just(openAIRequest), OpenAIRequest.class)
                .retrieve()
                .bodyToMono(OpenAIResponse.class)
                .block();

        if (openAIResponse == null || openAIResponse.getChoices().isEmpty()) {
            logger.error("OpenAI 응답이 비어 있습니다.");
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        String itineraryJson = openAIResponse.getChoices().get(0).getMessage().getContent();

        logger.info("OpenAI 응답: {}", itineraryJson);

        return itineraryJson;
    }

    /**
     * GPT로부터 받은 JSON 응답을 ItineraryResponse DTO로 변환합니다.
     *
     * @param itineraryJson GPT로부터 받은 여행 일정 JSON 문자열
     * @return ItineraryResponse 객체
     */
    public AITripPlanResponse parseItinerary(String itineraryJson) {
        try {
            AITripPlanResponse itineraryResponse = objectMapper.readValue(itineraryJson, AITripPlanResponse.class);
            return itineraryResponse;
        } catch (Exception e) {
            logger.error("여행 일정 JSON 파싱 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
