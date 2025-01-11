package com.umc.yeogi_gal_lae.api.vote.dto;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class VoteResponse<T> {

    private String code;
    private String message;
    private List<VoteDTO> data;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VoteDTO{
        private String id;        // 사용자 ID (userId)
        private String name;      // 사용자 이름 (userName)
        private String type;      // 투표 타입 (good, bad)
        private Integer count;    // 투표 수
    }
}
