package com.umc.yeogi_gal_lae.api.vote.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class VoteRequest {
    @NotNull(message = "투표한 userId")
    private final Long userId;

    @NotNull(message = "투표한 userName")
    private final String userName;

    @NotNull(message = "투표에 해당하는 여행 계획 Id")
    private final Long tripId;

    @NotNull(message = "투표 타입")
    private final String type;
}
