package com.umc.yeogi_gal_lae.api.user.service;

import static com.umc.yeogi_gal_lae.global.response.Code.INVALID_REFRESH_TOKEN;
import static com.umc.yeogi_gal_lae.global.response.Code.USER_NOT_AUTHENTICATED;
import static com.umc.yeogi_gal_lae.global.response.Code.USER_NOT_FOUND;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.exception.BusinessException;
import com.umc.yeogi_gal_lae.global.jwt.JwtToken;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;
import com.umc.yeogi_gal_lae.global.oauth.OAuthAttributes;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserService {


    private final UserRepository userRepository;
    private final JwtService jwtService;

    /**
     * 소셜 로그인 사용자 생성 메서드
     *
     * @param oAuthAttributes OAuth2 제공자로부터 받은 사용자 정보
     * @param email 사용자 이메일
     * @return 생성된 사용자 엔티티
     */
    public User createUser(OAuthAttributes oAuthAttributes, String email) {
        // 직접 이메일과 프로필 이미지를 가져옴
        String profileImage = oAuthAttributes.getProfileImage();

        User user = User.builder()
                .email(email)
                .profileImage(profileImage)
                .build();

        return userRepository.save(user);
    }

    /**
     * 현재 인증된 사용자의 정보를 가져오는 메서드
     *
     * @return 사용자 엔티티
     */
    public User getUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails)) {
            throw new BusinessException(USER_NOT_FOUND);
        }
        UserDetails userDetails = (UserDetails) principal;
        // 유저 정보 조회
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
    }

    /**
     * 액세스 토큰과 리프레시 토큰을 재발급하는 메서드
     *
     * @param accessToken 기존 액세스 토큰
     * @param refreshToken 기존 리프레시 토큰
     * @return 새로운 JWT 토큰
     */
    @Transactional
    public JwtToken reissueToken(String accessToken, String refreshToken) {
        // Refresh Token 검증
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new BusinessException(INVALID_REFRESH_TOKEN);
        }

        String email = jwtService.extractEmail(accessToken); // 이메일 추출 메서드

        if (email == null || email.isEmpty()) {
            throw new BusinessException(USER_NOT_AUTHENTICATED);
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new BusinessException(USER_NOT_AUTHENTICATED);
        }

        User user = userOpt.get();
        String userRefreshToken = user.getRefreshToken();

        // DB에 리프레시 토큰 없을 경우 (로그아웃 상태)
        if (userRefreshToken == null || !userRefreshToken.equals(refreshToken)) {
            throw new BusinessException(USER_NOT_AUTHENTICATED);
        }

        // 새로운 Access Token 생성
        String newAccessToken = jwtService.createAccessToken(email);
        // 새로운 Refresh Token 재발급
        String newRefreshToken = jwtService.reIssueRefreshToken(user);

        // 업데이트된 리프레시 토큰을 DB에 저장
        user.updateRefreshToken(newRefreshToken);
        userRepository.save(user);

        return JwtToken.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

}
