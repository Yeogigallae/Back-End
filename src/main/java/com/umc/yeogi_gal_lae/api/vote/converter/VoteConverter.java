package com.umc.yeogi_gal_lae.api.vote.converter;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.vote.dto.VoteResponse;

import java.util.Map;
import java.util.Optional;


public class VoteConverter {

    public static VoteResponse.VoteDTO convert( String type, User userVote, Map<String, Long> groupedVotes ) {

        // 현재 사용자가 특정 type('GOOD', 'BAD')에 투표했는지 확인
        Optional<User> filteredUserVote = Optional.ofNullable(userVote).filter(user -> user.getVote().getType().name().equals(type));

        return VoteResponse.VoteDTO.builder()
                .userId(filteredUserVote.map(User::getId).orElse(null))
                .userName(filteredUserVote.map(User::getUsername).orElse(null))
                .type(type)
                .count(groupedVotes.getOrDefault(type, 0L).intValue())
                .build();
    }
}
