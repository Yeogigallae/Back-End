package com.umc.yeogi_gal_lae.api.friendship.service;

import com.umc.yeogi_gal_lae.api.friendship.domain.Friendship;
import com.umc.yeogi_gal_lae.api.friendship.domain.FriendshipInvite;
import com.umc.yeogi_gal_lae.api.friendship.domain.FriendshipStatus;
import com.umc.yeogi_gal_lae.api.friendship.dto.FriendListResponse;
import com.umc.yeogi_gal_lae.api.friendship.repository.FriendshipInviteRepository;
import com.umc.yeogi_gal_lae.api.friendship.repository.FriendshipRepository;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final FriendshipInviteRepository friendshipInviteRepository;
    private final UserRepository userRepository;

    public String generateInviteUrl(Long inviterId) {
        // 랜덤 토큰 생성
        String token = UUID.randomUUID().toString();

        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new IllegalArgumentException("Inviter not found"));



        FriendshipInvite invite = FriendshipInvite.builder()
                .inviter(inviter)
                .token(token)
                .build();

        friendshipInviteRepository.save(invite);

        // 초대 URL 생성
        return "http://localhost:8080/friendship/accept?token=" + token;
    }


    @Transactional
    public void acceptInvite(String token, String inviteeEmail) {
        // 초대 정보 조회(invite 안에 inviter id 포함돼있음.)
        FriendshipInvite invite = friendshipInviteRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite token"));

        // invitee id 추출 optional로 정의돼있어서 필요 없는데 예외 처리
        User invitee = userRepository.findByEmail(inviteeEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invitee not found"));

        Friendship friendship = Friendship.builder()
                .inviter(invite.getInviter()) // ✅ User 객체 직접 설정
                .invitee(invitee) // ✅ 초대받은 User 객체 직접 설정
                .status(FriendshipStatus.ACCEPT)
                .build();



        friendshipRepository.save(friendship); // 새로운 친구 관계 저장

        // 초대 정보 삭제
        friendshipInviteRepository.delete(invite);
    }


    @Transactional
    public List<FriendListResponse> getFriendList(Long userId) {
        List<Friendship> friendships = friendshipRepository.findByInviterIdOrInviteeId(userId, userId);

        Set<FriendListResponse> friendSet = new HashSet<>();

        for (Friendship friendship : friendships) {
            Long friendId = friendship.getInviter().getId().equals(userId)
                    ? friendship.getInvitee().getId()
                    : friendship.getInviter().getId();

            User friend = userRepository.findById(friendId).orElse(null);

            FriendListResponse friendResponse = FriendListResponse.builder()
                    .friendId(friendId)
                    .friendName(friend != null ? friend.getUsername() : "Unknown")
                    .profileImageUrl(friend != null ? friend.getProfileImage() : null)
                    .build();

            friendSet.add(friendResponse); // 자동으로 중복 제거됨
        }

        return new ArrayList<>(friendSet); // Set을 다시 List로 변환하여 반환
    }

}