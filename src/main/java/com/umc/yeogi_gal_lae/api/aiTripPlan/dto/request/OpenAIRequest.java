package com.umc.yeogi_gal_lae.api.aiTripPlan.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
public class OpenAIRequest {

    private String model;
    private List<Message> messages;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}
