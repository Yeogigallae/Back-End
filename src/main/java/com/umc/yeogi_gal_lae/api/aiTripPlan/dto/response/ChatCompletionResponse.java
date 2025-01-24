package com.umc.yeogi_gal_lae.api.aiTripPlan.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatCompletionResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choice> choices;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Choice {
        private Integer index;
        private ChatMessage message;
        private String finish_reason;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class ChatMessage {
        private String role;    // "assistant"
        private String content; // 최종 응답 텍스트
    }
}
