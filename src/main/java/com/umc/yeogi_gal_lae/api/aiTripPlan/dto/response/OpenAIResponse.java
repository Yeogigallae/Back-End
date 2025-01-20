package com.umc.yeogi_gal_lae.api.aiTripPlan.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class OpenAIRequest {
    private String model;
    private List<Message> messages;

    @Data
    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}

@Data
public class OpenAIResponse {
    private String id;
    private String object;
    private int created;
    private List<Choice> choices;
    private Usage usage;

    @Data
    public static class Choice {
        private int index;
        private Message message;
        private String finish_reason;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
    }

    @Data
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }
}
