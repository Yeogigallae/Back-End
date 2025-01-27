package com.umc.yeogi_gal_lae.api.friendship.service;

import com.umc.yeogi_gal_lae.api.friendship.domain.Friendship;
import com.umc.yeogi_gal_lae.api.friendship.domain.FriendshipInvite;
import com.umc.yeogi_gal_lae.api.friendship.repository.FriendshipRelationRepository;
import com.umc.yeogi_gal_lae.api.friendship.repository.FriendshipRepository;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final FriendshipRelationRepository friendshipRelationRepository;
    private final UserRepository userRepository;

    public String generateInviteUrl(Long inviterId, String inviteeEmail) {
        // 랜덤 토큰 생성
        String token = UUID.randomUUID().toString();

        // 초대 정보 저장
        FriendshipInvite invite = new FriendshipInvite();
        invite.setInviterId(inviterId);
        invite.setInviteeEmail(inviteeEmail);
        invite.setToken(token);
        invite.setCreatedAt(LocalDateTime.now());
        friendshipRepository.save(invite);

        // 초대 URL 생성
        return "https://example.com/friendship?token=" + token;
    }

    public void acceptInvite(String token) {
        // 초대 정보 조회
        FriendshipInvite invite = friendshipRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite token"));

        // 요청을 보낸 사용자와 받은 사용자 조회
        User inviter = userRepository.findById(invite.getInviterId())
                .orElseThrow(() -> new IllegalArgumentException("Inviter not found"));
        User invitee = userRepository.findByEmail(invite.getInviteeEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invitee not found"));

        // 친구 관계 생성
        Friendship relation = new Friendship();
        relation.setUser(inviter); // 요청을 보낸 사용자
        relation.setFriend(invitee); // 요청을 받은 사용자
        friendshipRelationRepository.save(relation);

        // 초대 정보 삭제
        friendshipRepository.delete(invite);
    }
}