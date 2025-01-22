package com.umc.yeogi_gal_lae.api.AITripPlan.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OpenAIRequest {

    private String model;
    private List<Message> messages;

    @Getter
    @Builder
    public static class Massage {
        private String role;
        private String content;
    }

    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;

    }
}
