package com.umc.yeogi_gal_lae.api.vote.dto;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VoteResponse {

    private Long userId;      // 현재 접속 중인 유저가 투표한 type 에만 Id와 Name 포함하여 반환
    private String userName;
    private String type;      // GOOD or BAD
    private Integer count;
}
