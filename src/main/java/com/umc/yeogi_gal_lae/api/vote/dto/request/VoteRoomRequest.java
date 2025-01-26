package com.umc.yeogi_gal_lae.api.vote.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class VoteRoomRequest {

    @NotNull
    private Long tripId;

    @NotNull
    private Long roomId;

    @NotNull
    private Long voteRoomId;

}