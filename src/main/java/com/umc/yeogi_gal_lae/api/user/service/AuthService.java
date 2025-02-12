package com.umc.yeogi_gal_lae.api.user.service;

import com.umc.yeogi_gal_lae.api.friendship.repository.FriendshipInviteRepository;
import com.umc.yeogi_gal_lae.api.friendship.repository.FriendshipRepository;
import com.umc.yeogi_gal_lae.api.room.repository.RoomMemberRepository;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.api.tripPlan.repository.TripPlanRepository;
import com.umc.yeogi_gal_lae.api.user.converter.AuthConverter;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.api.vote.AuthenticatedUserUtils;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRepository;
import com.umc.yeogi_gal_lae.api.vote.repository.VoteRoomRepository;
import com.umc.yeogi_gal_lae.global.common.response.BaseResponse;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.global.jwt.JwtUtil;
import com.umc.yeogi_gal_lae.global.oauth.dto.KakaoDTO;
import com.umc.yeogi_gal_lae.global.oauth.util.CookieUtil;
import com.umc.yeogi_gal_lae.global.oauth.util.KakaoUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoUtil kakaoUtil;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomRepository roomRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendshipInviteRepository friendshipInviteRepository;
    private final TripPlanRepository tripPlanRepository;
    private final VoteRoomRepository voteRoomRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public User oAuthLogin(String accessCode, HttpServletResponse httpServletResponse) {
        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);

        String email = kakaoProfile.getKakao_account().getEmail();
        String nickname = kakaoProfile.getKakao_account().getProfile().getNickname();
        String profileImage = kakaoProfile.getKakao_account().getProfile().getProfile_image_url();

        // 기존 사용자 조회
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user = optionalUser.orElseGet(() -> createNewUser(email, nickname, profileImage));

        // JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(user.getEmail());
        String refreshToken = jwtUtil.createRefreshToken(user.getEmail());

        // 유저 엔티티에 저장
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);
        user.setProfileImage(profileImage);
        userRepository.save(user);

        // 쿠키로 토큰 저장
        CookieUtil.addCookie(httpServletResponse, "accessToken", accessToken, (int) jwtUtil.getAccessTokenValidity());
        CookieUtil.addCookie(httpServletResponse, "refreshToken", refreshToken, (int) jwtUtil.getRefreshTokenValidity());

        String token = jwtUtil.createAccessToken(user.getEmail());
        httpServletResponse.setHeader("Authorization", token);

        return user;
    }

    private User createNewUser(String email, String nickname, String profileImage) {
        return userRepository.save(AuthConverter.toUser(email, nickname, profileImage));
    }


    @Transactional
    public BaseResponse<String> deleteUser(HttpServletResponse response) {
        String email = AuthenticatedUserUtils.getAuthenticatedUserEmail();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // User 엔티티에서 vote 참조를 제거 (vote_id = null)
        userRepository.detachVoteFromUser(user);

        // TripPlan의 VoteRoom 참조 해제
        tripPlanRepository.detachVoteRoomByUser(user);

        // Vote 삭제 (VoteRoom -> Vote 순으로)
        voteRepository.deleteByVoteRoomUser(user);
        voteRoomRepository.deleteByTripPlanUser(user);

        // TripPlan 삭제
        tripPlanRepository.deleteByUser(user);

        // 방 멤버 및 방 삭제
        roomMemberRepository.deleteByUser(user);
        roomRepository.deleteByMaster(user);

        // 친구 관련 데이터 삭제
        friendshipInviteRepository.deleteByInviterOrInvitee(user, user);
        friendshipRepository.deleteByInviterOrInvitee(user, user);

        // 최종적으로 User 삭제
        userRepository.delete(user);

        // 최종적으로 User 삭제
        userRepository.delete(user);

        // JWT 쿠키 삭제
        CookieUtil.deleteCookie(response, "accessToken");
        CookieUtil.deleteCookie(response, "refreshToken");

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();

        return BaseResponse.onSuccess("회원 탈퇴 성공");
    }

}