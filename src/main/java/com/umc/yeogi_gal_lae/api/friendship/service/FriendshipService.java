package com.umc.yeogi_gal_lae.api.friendship.service;

import com.umc.yeogi_gal_lae.api.friendship.domain.Friendship;
import com.umc.yeogi_gal_lae.api.friendship.domain.FriendshipInvite;
import com.umc.yeogi_gal_lae.api.friendship.domain.FriendshipStatus;
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
    private final UserRepository userRepository;

    public String generateInviteUrl(Long inviterId) {
        // 랜덤 토큰 생성
        String token = UUID.randomUUID().toString();

        FriendshipInvite invite = FriendshipInvite.builder()
                .inviterId(inviterId)
                .token(token)
                .createdAt(LocalDateTime.now())
                .build();

        friendshipRepository.save(invite);

        // 초대 URL 생성
        return "http://localhost:8080/friendship/accept?token=" + token;    }



    public void acceptInvite(String token, String inviteeEmail) {
        // 초대 정보 조회(invite 안에 inviter id 포함돼있음.)
        FriendshipInvite invite = friendshipRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite token"));

        // invitee id 추출 optional로 정의돼있어서 필요 없는데 예외 처리
        User invitee = userRepository.findByEmail(inviteeEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invitee not found"));

        Friendship friendship = Friendship.builder()
                .inviterId(invite.getInviterId())
                .inviteeId(invitee.getId())
                .status(FriendshipStatus.ACCEPT) // 필요하면 초기값 설정
                .build();

        // 초대 정보 삭제
        friendshipRepository.delete(invite);
    }
}