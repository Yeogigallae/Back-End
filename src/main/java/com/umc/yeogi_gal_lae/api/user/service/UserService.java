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
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.CustomOAuth2User;
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

    public User createUser(OAuthAttributes attrs, String email) {
        // 1) 로깅: 어떤 이메일 / 닉네임으로 유저를 생성하려고 하는지
        log.info("createUser() called with email={} nickname={} profileImage={}",
                email, attrs.getName(), attrs.getProfileImage());

        // 2) 엔티티 빌드
        User newUser = User.builder()
                .email(email)
                .username(attrs.getName())      // 카카오/구글의 닉네임 -> DB username
                .profileImage(attrs.getProfileImage())
                .build();

        // 3) 실제 DB 저장
        User savedUser = userRepository.save(newUser);

        // 4) 저장 완료 후 로그: userId 확인
        log.info("New user saved. userId={}, email={}, profileImage={}",
                savedUser.getId(), savedUser.getEmail(), savedUser.getProfileImage());

        return savedUser;
    }

    /**
     * 현재 인증된 사용자를 반환
     * (예: SecurityContext에서 email 추출 후 findByEmail)
     */
    public User getUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) {
            throw new BusinessException(USER_NOT_FOUND);
        }
        String email = userDetails.getUsername();
        // DB에서 사용자 조회
        return userRepository.findByEmail(email)
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
