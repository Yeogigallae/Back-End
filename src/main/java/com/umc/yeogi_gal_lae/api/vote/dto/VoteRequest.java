package com.umc.yeogi_gal_lae.api.vote.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

@Builder
@Getter @Setter
public class VoteRequest {

    @JsonIgnore      // 클라이언트에서 받지 않음.
    private String userEmail;

    @NotNull(message = "투표에 해당하는 여행 계획 Id")
    private Long tripId;

    @NotNull(message = "투표 타입")
    private  String type;
}
