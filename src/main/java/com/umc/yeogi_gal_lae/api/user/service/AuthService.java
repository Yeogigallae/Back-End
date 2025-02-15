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
import com.umc.yeogi_gal_lae.global.error.AuthHandler;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.global.error.ErrorStatus;
import com.umc.yeogi_gal_lae.global.jwt.JwtUtil;
import com.umc.yeogi_gal_lae.global.oauth.dto.KakaoDTO;
import com.umc.yeogi_gal_lae.global.oauth.util.CookieUtil;
import com.umc.yeogi_gal_lae.global.oauth.util.KakaoUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
    public User oAuthLogin(String accessCode, String redirectUri, HttpServletResponse httpServletResponse) {
        try {
            KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode, redirectUri);
            KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);

            String email = kakaoProfile.getKakao_account().getEmail();
            String nickname = kakaoProfile.getKakao_account().getProfile().getNickname();
            String profileImage = kakaoProfile.getKakao_account().getProfile().getProfile_image_url();

            User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(email, nickname, profileImage));

            // JWT 토큰 생성
            String accessToken = jwtUtil.createAccessToken(user.getEmail());
            String refreshToken = jwtUtil.createRefreshToken(user.getEmail());

            user.setAccessToken(accessToken);
            user.setRefreshToken(refreshToken);
            user.setProfileImage(profileImage);
            userRepository.save(user);

            // 쿠키 저장
            CookieUtil.addCookie(httpServletResponse, "accessToken", accessToken, (int) jwtUtil.getAccessTokenValidity());
            CookieUtil.addCookie(httpServletResponse, "refreshToken", refreshToken, (int) jwtUtil.getRefreshTokenValidity());

            httpServletResponse.setHeader("Authorization", accessToken);

            return user;

        } catch (AuthHandler e) {
            throw e;
        } catch (Exception e) {
            throw new AuthHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private User createNewUser(String email, String nickname, String profileImage) {
        try {
            return userRepository.save(AuthConverter.toUser(email, nickname, profileImage));
        } catch (Exception e) {
            throw new AuthHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
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


    public User getUser() {
        // SecurityContext에서 Authentication 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // principal을 User로 캐스팅
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 이미 DB 조회된 User 엔티티
        User user = (User) principal;
        return user;
    }

}