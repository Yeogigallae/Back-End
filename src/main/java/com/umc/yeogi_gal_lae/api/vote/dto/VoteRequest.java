package com.umc.yeogi_gal_lae.api.vote.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class VoteRequest {
    @NotNull(message = "투표한 사용자의 Id")
    private Long userId;

    @NotNull(message = "투표한 사용자의 이름")
    private String userName;

    @NotNull(message = "투표에 해당하는 여행 계획 Id")
    private Long tripId;

    @NotNull(message = "투표 타입")
    private  String type;
}
