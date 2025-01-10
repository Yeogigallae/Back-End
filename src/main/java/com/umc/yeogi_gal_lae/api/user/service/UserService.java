package com.umc.yeogi_gal_lae.api.user.service;

import static com.umc.yeogi_gal_lae.global.response.Code.INVALID_REFRESH_TOKEN;
import static com.umc.yeogi_gal_lae.global.response.Code.USER_NOT_AUTHENTICATED;
import static com.umc.yeogi_gal_lae.global.response.Code.USER_NOT_FOUND;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.dto.request.SignUpRequest;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.exception.BusinessException;
import com.umc.yeogi_gal_lae.global.jwt.JwtToken;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;
import com.umc.yeogi_gal_lae.global.oauth.OAuthAttributes;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.OAuth2UserInfo;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserService {


    private final UserRepository userRepository;
    private final JwtService jwtService;

    // 소셜 로그인 사용자 생성 메서드
    public User createUser(OAuthAttributes oAuthAttributes, String email) {
        String profileImage = oAuthAttributes.getProfileImage();

        User user = User.builder()
                .email(email)
                .profileImage(profileImage)
                .refreshToken(null)
                .build();

        return userRepository.save(user);
    }

    public User getUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 유저 정보 조회
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
    }

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

    public void updateRefreshToken(User user, String refreshToken) {
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);
    }

    public String generateRefreshToken() {
        // Refresh Token 생성 로직 (예: UUID, JWT 등)
        return java.util.UUID.randomUUID().toString();
    }

}
