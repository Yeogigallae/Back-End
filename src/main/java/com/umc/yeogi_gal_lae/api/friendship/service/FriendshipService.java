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
import org.springframework.beans.factory.annotation.Value;import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final FriendshipInviteRepository friendshipInviteRepository;
    private final UserRepository userRepository;
    @Value("${invite.url}") // 설정 파일에서 초대 URL 읽기
    private String inviteUrl;

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
        return inviteUrl + "?token=" + token;
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
                .inviter(invite.getInviter()) // User 객체 직접 설정
                .invitee(invitee) // 초대받은 User 객체 직접 설정
                .status(FriendshipStatus.ACCEPT)
                .build();



        friendshipRepository.save(friendship); // 새로운 친구 관계 저장

        // 초대 정보 삭제
        friendshipInviteRepository.delete(invite);
    }


    @Transactional
    public List<FriendListResponse> getFriendList(Long userId) {
        // 친구 관계를 조회: 사용자가 inviter이거나 invitee인 모든 관계를 가져옵니다.
        List<Friendship> friendships = friendshipRepository.findByInviterIdOrInviteeId(userId, userId);

// 친구 ID 리스트를 Stream을 사용하여 변환
        List<Long> friendIds = friendships.stream()
                .map(friendship -> friendship.getInviter().getId().equals(userId)
                        ? friendship.getInvitee().getId()
                        : friendship.getInviter().getId())
                .toList();

        // 수집한 친구 ID로 사용자 정보를 한 번에 조회 (성능 최적화)
        List<User> friends = userRepository.findAllById(friendIds);

        // User 객체를 기반으로 FriendListResponse 생성
        List<FriendListResponse> friendResponses = friends.stream()
                .map(friend -> FriendListResponse.builder()
                        .friendId(friend.getId()) // 친구 ID
                        .friendName(Optional.ofNullable(friend.getUsername()).orElse("Unknown")) // 이름 (Optional로 처리)
                        .profileImageUrl(Optional.ofNullable(friend.getProfileImage()).orElse(null)) // 프로필 이미지 (Optional)
                        .build())
                .toList();

        // 중복된 친구를 제거하고 반환
        return friendResponses.stream().distinct().toList();
    }

    // 내가 초대한 친구 목록 조회
    public List<User> getInvitedFriends(Long userId) {
        List<Friendship> friendships = friendshipRepository.findInvitedFriends(userId);
        return friendships.stream().map(Friendship::getInvitee).collect(Collectors.toList());
    }

    // 내가 초대받은 친구 목록 조회
    public List<User> getReceivedFriends(Long userId) {
        List<Friendship> friendships = friendshipRepository.findReceivedFriends(userId);
        return friendships.stream().map(Friendship::getInviter).collect(Collectors.toList());
    }

}