package com.umc.yeogi_gal_lae.api.aiTripPlan.dto.request;

public class OpenAIRequest {
    private String model;
    private List<Message> messages;

    public static class Messgae {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
