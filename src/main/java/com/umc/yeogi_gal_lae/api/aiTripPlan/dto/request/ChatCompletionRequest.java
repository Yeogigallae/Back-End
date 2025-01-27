package com.umc.yeogi_gal_lae.api.aiTripPlan.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatCompletionRequest {
    private String model;           // e.g. "gpt-3.5-turbo"
    private List<Message> messages; // 대화 메시지들
    private Integer max_tokens;     // tokens 제한
    private Double temperature;     // creativity

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String role;        // "system", "user", "assistant"
        private String content;
    }
}
