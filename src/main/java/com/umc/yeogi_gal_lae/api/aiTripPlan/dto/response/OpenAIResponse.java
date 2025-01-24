package com.umc.yeogi_gal_lae.api.aiTripPlan.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class OpenAIResponse {
    private String id;
    private String object;
    private int created;
    private List<Choice> choices;
    private Usage usage;

    @Getter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class Choice {
        private int index;
        private Message message;
        private String finish_reason;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class Message {
        private String role;
        private String content;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }
}
